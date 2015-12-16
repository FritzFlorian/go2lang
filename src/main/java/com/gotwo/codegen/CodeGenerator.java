package com.gotwo.codegen;

import com.gotwo.parser.*;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by florian on 10/12/15.
 */
public class CodeGenerator implements Opcodes{
    private static final String METHOD_INIT_SCOPE = "initScopeWithId";
    private static final String METHOD_INIT_SCOPE_PARAMETERS = "(ILcom/gotwo/codegen/Scope;)Lcom/gotwo/codegen/Scope;";

    private ParsingResult parsingResult;


    public CodeGenerator(ParsingResult parsingResult) {
        this.parsingResult = parsingResult;

    }

    public void generateClassFile(String targetPath, String className) {
        String target = targetPath + "/com/gotwo/" + className + ".class";
        try {
            File file = new File(target);
            if(file.exists()) {
                file.delete();
            }
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(target);
            fos.write(generateJavaByteCode("com/gotwo/" + className));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] generateJavaByteCode(String className) {
        ClassWriter cw = new ClassWriter(0);


        //Create the class in the right package
        visitClassDeclaration(cw, className);

        //Init the fields (attributes)
        visitFields(cw);

        //Add an empty constructor
        visitConstructor(cw);

        if(parsingResult.getTargetLabels().get("start") != null) {
            //This is an runnable go2 program
            //Add an main method that launches the program
            visitMainMethod(cw, className);
        }

        visitInitScopeWithIdMethod(cw);
        visitRunMethod(cw, className);


        cw.visitEnd();
        return cw.toByteArray();
    }

    private void visitFields(ClassWriter cw) {
        //Add an currentScope attribute that is used during execution of the code
        FieldVisitor fv = cw.visitField(ACC_PRIVATE, "currentScope", "Lcom/gotwo/codegen/Scope;", null, null);
        fv.visitEnd();
    }

    private void visitClassDeclaration(ClassWriter cw, String className) {
        cw.visit(49,
                ACC_PUBLIC + ACC_SUPER,
                className,
                null,
                "java/lang/Object",
                null);
    }

    private void visitConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void visitMainMethod(ClassWriter cw, String className) {
        //Create the method
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null);
        mv.visitCode();
        //Print out a message
        mv.visitFieldInsn(GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Starting go2 program...");
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(Ljava/lang/String;)V",
                false);



        //Here we start, above is simply init and a "greeting" to make sure our program runs

        //Init a new instance of this class...
        mv.visitTypeInsn(NEW, className);
        mv.visitInsn(DUP); //Duplicate the reference, will need this later on
        mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "()V", false); //Simple call to default constructor

        //Call run on it
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);



        //Return from the main method
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    private void visitInitScopeWithIdMethod(ClassWriter cw) {
        //Create the method
        //public Scope initScopeWithId(int id, Scope parentScope)
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC,
                METHOD_INIT_SCOPE,
                METHOD_INIT_SCOPE_PARAMETERS,
                null,
                null);
        mv.visitCode();
        //Stack starts empty
        //Variables
        //0 = this
        //1 = ScopeId
        //2 = parentScope
        //3 = newScope

        //Init a new scope object and leave one reference on top of the stack
        mv.visitTypeInsn(NEW, "com/gotwo/codegen/Scope");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, "com/gotwo/codegen/Scope", "<init>", "(Lcom/gotwo/codegen/Scope;)V", false);

        //Store it in variable 3
        //Variable 3 = newScope
        mv.visitVarInsn(ASTORE, 3);

        //Here goes the real logic, we model the switch that will init
        //the right local variables for the given scope
        Label afterSwitch = new Label();

        //Switch statement code
        {
            //Load the 'switched' variable
            mv.visitVarInsn(ILOAD, 1);

            Map<ScopeNode, Label> labels = new HashMap<>();
            //We need one branch for each scope
            for(ScopeNode scopeNode : parsingResult.getScopeNodes()) {
                labels.put(scopeNode, new Label());
            }

            Set<Map.Entry<ScopeNode, Label>> labelSet = labels.entrySet();
            List<Map.Entry<ScopeNode, Label>> sortedLabelSet =
                    labelSet.stream().sorted((o1, o2) -> o1.getKey().getId() - o2.getKey().getId()).collect(Collectors.toList());

            int[] values = new int[labelSet.size()];
            Label[] labelArray = new Label[labelSet.size()];
            int i = 0;
            for(Map.Entry<ScopeNode, Label> labelEntry : sortedLabelSet) {
                //Lets create a branch for each possible scope
                values[i] = labelEntry.getKey().getId();
                labelArray[i] = labelEntry.getValue();
                i++;
            }

            //We successfully created the switch head
            mv.visitLookupSwitchInsn(afterSwitch, values, labelArray);
            //Les implement all the different branches
            for(Map.Entry<ScopeNode, Label> labelEntry : sortedLabelSet) {
                visitSwitchBranch(mv, labelEntry.getValue(), labelEntry.getKey());
                //We want a break after each case, so go to the end
                mv.visitJumpInsn(GOTO, afterSwitch);
            }
        }

        //Finished all our switch logic
        //Insert the jump label to mark this
        mv.visitLabel(afterSwitch);


        //Load the newly created scope...
        mv.visitVarInsn(ALOAD, 3);
        //...and finally return the reference
        mv.visitInsn(ARETURN);
        mv.visitMaxs(4, 4);
        mv.visitEnd();
    }

    private void visitSwitchBranch(MethodVisitor mv, Label label, ScopeNode scopeNode) {
        mv.visitLabel(label);

        //Load the newScope that we want to work on
        mv.visitVarInsn(ALOAD, 3);

        for(IntegerDeclaration integerDeclaration : scopeNode.getIntegerDeclarations().values()) {
            //Duplicate the newScope reference on the stack as we will consume one
            mv.visitInsn(DUP);
            //Get the variable name
            mv.visitLdcInsn(integerDeclaration.getName());
            //Get the variables default value
            mv.visitLdcInsn(new Integer(integerDeclaration.getValue()));
            //add it to the scope
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/gotwo/codegen/Scope", "setLocalIntegerVariable", "(Ljava/lang/String;I)V", false);
        }

        //remove the last remaining reference to newScope from the stack
        mv.visitInsn(POP);
    }

    private static final int CURRENT_SCOPE = 1;
    private void visitRunMethod(ClassWriter cw, String className) {
        int maxStackHeight = 4;
        int tempStackHeight = 0;

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
        mv.visitCode();

        //Load the current scope to an local variable
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, "currentScope", "Lcom/gotwo/codegen/Scope;");
        mv.visitVarInsn(ASTORE, CURRENT_SCOPE);

        //Here comes the actual work
        //Convert the AST to bytecode
        //Lets do this recursive for sub-scopes
        tempStackHeight = generateScopeNodeCode(mv ,parsingResult.getRootScope(), className);

        if(tempStackHeight > maxStackHeight) {
            maxStackHeight = tempStackHeight;
        }

        //Return from the call
        mv.visitInsn(RETURN);
        mv.visitMaxs(maxStackHeight, 2);
        mv.visitEnd();
    }

    /**
     * @return The maximum used stack height
     */
    private int generateScopeNodeCode(MethodVisitor mv, ScopeNode scopeNode, String className) {
        int localStackHeight = 4;
        int tempStackHeight = 0;

        //Generate the current scope
        //At this point the stack should be empty
        mv.visitVarInsn(ALOAD, 0);
        mv.visitIntInsn(SIPUSH, scopeNode.getId());
        mv.visitVarInsn(ALOAD, CURRENT_SCOPE);
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "initScopeWithId", "(ILcom/gotwo/codegen/Scope;)Lcom/gotwo/codegen/Scope;", false);
        mv.visitVarInsn(ASTORE, CURRENT_SCOPE);
        //Stack is empty again, ready to do some work


        //We simply generate one statement after another, that simple
        for(Node node : scopeNode.getChildNodes()) {
            switch (node.getType()) {
                case SCOPE:
                    tempStackHeight = generateScopeNodeCode(mv, (ScopeNode)node, className);
                    if(tempStackHeight > localStackHeight) {
                        localStackHeight = tempStackHeight;
                    }
                    break;

                case ASSIGNMENT:
                    tempStackHeight = generateAssignment(mv, (AssignmentNode)node, className);
                    if(tempStackHeight > localStackHeight) {
                        localStackHeight = tempStackHeight;
                    }
                    break;

                case CONDITION:
                    tempStackHeight = generateCondition(mv, (ConditionNode) node, className);
                    if(tempStackHeight > localStackHeight) {
                        localStackHeight = tempStackHeight;
                    }
                    break;

                case GOTO:
                    break;
            }
        }

        return localStackHeight;
    }

    /**
     * @return The maximum used stack height
     */
    private int generateCondition(MethodVisitor mv, ConditionNode conditionNode, String className) {
        switch (conditionNode.getBranches()) {
            case IF:
                return generateIfCondition(mv, (IfConditionNode) conditionNode, className);
            case IFELSE:
                return 0;
        }

        return 0;
    }

    /**
     * @return The maximum used stack height
     */
    private int generateIfCondition(MethodVisitor mv, IfConditionNode ifConditionNode, String className) {
        Label endLabel = new Label();
        int stackHeight, tempStackHeight;

        stackHeight = generateExpression(mv, ifConditionNode.getExpression(), className, 0);
        mv.visitJumpInsn(IFEQ, endLabel); //Go to end if it is equal to zero
        tempStackHeight = generateScopeNodeCode(mv, ifConditionNode.getIfScope(), className); //otherwise run a new scope...
        mv.visitLabel(endLabel);

        if(stackHeight > tempStackHeight) {
            return stackHeight;
        } else  {
            return tempStackHeight;
        }
    }

    /**
     * @return The maximum used stack height
     */
    private int generateAssignment(MethodVisitor mv, AssignmentNode assigmentNode, String className) {
        //Quite simple to get an assignment done
        //Evaluate the right hand side expression(not job of this method)
        //Then simply assign the result to the right variable

        mv.visitVarInsn(ALOAD, CURRENT_SCOPE);
        mv.visitLdcInsn(assigmentNode.getIntegerDeclaration().getName());
        int stackHeight = generateExpression(mv, assigmentNode.getExpressionNode(), className, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/gotwo/codegen/Scope", "setIntegerValue", "(Ljava/lang/String;I)V", false);
        //Stack should be empty at this point

        return stackHeight + 2;
    }

    /**
     * Generates the code to evaluate an expression.
     * The expression result will be put on top of the current stack.
     *
     * NOTE: Complicated sub-expressions will require stack space, so take note of it
     * and consider it in the visitMax() call
     *
     * @return The maximum used stack height
     */
    private int generateExpression(MethodVisitor mv, ExpressionNode expressionNode, String className, int stackDepth) {
        int localStackHeight = stackDepth;
        int tempStackHeight = 0;

        if(expressionNode instanceof ExpressionNode.SubExpressionNode) {
            //An expression that has 2 sub values and an operator connecting them
            ExpressionNode.SubExpressionNode subExpressionNode = (ExpressionNode.SubExpressionNode)expressionNode;

            localStackHeight = generateExpression(mv, subExpressionNode.getLeft(), className, stackDepth);
            tempStackHeight = generateExpression(mv, subExpressionNode.getRight(), className, stackDepth + 1);

            if(tempStackHeight > localStackHeight) {
                localStackHeight = tempStackHeight;
            }

            switch (subExpressionNode.getOperator().getOp()) {
                case ADD:
                    mv.visitInsn(IADD);
                    break;
                case SUB:
                    mv.visitInsn(ISUB);
                    break;
                case DIV:
                    mv.visitInsn(IDIV);
                    break;
                case MUL:
                    mv.visitInsn(IMUL);
                    break;
                case EQU:
                case NOTEQU:
                case LESS:
                case GREATER:
                case LESSEQU:
                case GREATEREQU:
                case NOT:
                    mv.visitInsn(ISUB);
                    mv.visitInsn(ICONST_1); //Default is true
                    mv.visitInsn(SWAP);

                    Label trueLabel = new Label(); //We jump here if the expression is true
                    //Now we have to decide when to jump to true or false
                    switch (subExpressionNode.getOperator().getOp()) {
                        case EQU:
                            mv.visitJumpInsn(IFEQ, trueLabel);
                            break;
                        case NOTEQU:
                            mv.visitJumpInsn(IFNE, trueLabel);
                            break;
                        case GREATER:
                            mv.visitJumpInsn(IFGT, trueLabel);
                            break;
                        case GREATEREQU:
                            mv.visitJumpInsn(IFGE, trueLabel);
                            break;
                        case LESS:
                            mv.visitJumpInsn(IFLT, trueLabel);
                            break;
                        case LESSEQU:
                            mv.visitJumpInsn(IFLE, trueLabel);
                            break;
                    }

                    mv.visitInsn(POP);
                    mv.visitInsn(ICONST_0); // Set result to false
                    mv.visitLabel(trueLabel); //Jump over "set false" instructions

                    break;
            }
        } else if(expressionNode instanceof ExpressionNode.IntExpressionNode){
            ExpressionNode.IntExpressionNode intExpressionNode = (ExpressionNode.IntExpressionNode)expressionNode;
            mv.visitVarInsn(ALOAD, CURRENT_SCOPE);
            mv.visitLdcInsn(intExpressionNode.getIntegerDeclaration().getName());
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/gotwo/codegen/Scope", "getIntegerValue", "(Ljava/lang/String;)I", false);

            localStackHeight = stackDepth + 2;
        } else {
            ExpressionNode.ConstIntExpressionNode constIntExpressionNode = (ExpressionNode.ConstIntExpressionNode) expressionNode;
            mv.visitLdcInsn(new Integer(constIntExpressionNode.getValue()));

            localStackHeight = stackDepth + 1;
        }

        return localStackHeight;
    }
}
