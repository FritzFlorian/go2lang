package com.gotwo.codegen;

import com.gotwo.error.UndeclearedIdentifier;
import com.gotwo.parser.*;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by florian on 10/12/15.
 *
 * This will turn results form the parser in executable bytecode.
 * Might seem like the magic of compilers happens here,
 * but you will very soon notice that (despite possible performance optimizing)
 * there really is nothing complex in here. Simply spill out bytecode statement
 * after statement. As long as each simple, individual part does its job
 * everything will work out in the end.
 */
public class CodeGenerator implements Opcodes{
    //The name of the initScopeWithId method
    private static final String METHOD_INIT_SCOPE = "initScopeWithId";
    //The Parameter list of the initScopeWithId method
    private static final String METHOD_INIT_SCOPE_PARAMETERS = "(ILcom/gotwo/codegen/Scope;)Lcom/gotwo/codegen/Scope;";
    //The position of the current_scope local variable in the run method
    private static final int CURRENT_SCOPE = 1;

    private ParsingResult parsingResult;

    private String className;


    public CodeGenerator(ParsingResult parsingResult, String className) {
        this.parsingResult = parsingResult;
        this.className = className;
    }

    /**
     * Generates bytecode out of the given parsing Result.
     * Will save it in the target Path.
     *
     * @param targetPath The root path of the output.
     * @throws UndeclearedIdentifier
     */
    public void generateClassFile(String targetPath) throws UndeclearedIdentifier {
        String target = targetPath + className + ".class";

        try {
            File file = new File(target);
            if(file.exists()) {
                file.delete();
            }
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(target);
            fos.write(generateJavaByteCode());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates executable Java bytecode out of the given Parsing result.
     *
     * @return The generated Java bytecode
     * @throws UndeclearedIdentifier
     */
    public byte[] generateJavaByteCode() throws UndeclearedIdentifier {
        ClassWriter cw = new ClassWriter(0);

        //Create the class in the right package
        visitClassDeclaration(cw);
        //Init the fields (attributes)
        visitFields(cw);
        //Add an empty constructor
        visitConstructor(cw);

        if(parsingResult.getTargetLabels().get("start") != null) {
            //This is an runnable go2 program
            //Add an main method that launches the program
            visitMainMethod(cw);
        }

        //Add method to generate an given scope with default variables
        visitInitScopeWithIdMethod(cw);
        //Add the actual program logic
        visitRunMethod(cw);

        cw.visitEnd();

        return cw.toByteArray();
    }

    /**
     * Adds all the needed attributes to the class.
     *
     * @param cw The current ClassWriter instance.
     */
    private void visitFields(ClassWriter cw) {
        //Add an currentScope attribute that is used during execution of the code
        FieldVisitor fv = cw.visitField(ACC_PRIVATE, "currentScope", "Lcom/gotwo/codegen/Scope;", null, null);
        fv.visitEnd();
    }

    /**
     * Declares the class with the given name.
     *
     * @param cw The current ClassWriter instance.
     */
    private void visitClassDeclaration(ClassWriter cw) {
        cw.visit(49,
                ACC_PUBLIC + ACC_SUPER,
                className,
                null,
                "java/lang/Object",
                null);
    }

    /**
     * Adds the default constructor for the class.
     * Has no logic in it, its simply required by the JVM.
     *
     * @param cw The current ClassWriter instance.
     */
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

    /**
     * Adds an main method to the class.
     * This will run the go2 program in an clean scope if called.
     *
     * @param cw The current ClassWriter instance.
     */
    private void visitMainMethod(ClassWriter cw) {
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

    /**
     * Adds a method that inits an given scope by its id.
     * This will set all default variables for the scope.
     * Can optionally add other scope setup logic.
     *
     * @param cw The current ClassWriter instance.
     */
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
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "com/gotwo/codegen/Scope", "<init>", "(Lcom/gotwo/codegen/Scope;I)V", false);

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
                    labelSet.stream()
                            .sorted((o1, o2) -> o1.getKey().getId() - o2.getKey().getId()).collect(Collectors.toList());

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

    /**
     * This method will add the init code of a given ScopeNode
     * in form of an switch branch.
     * It is called in the initBranchWithId method to generate
     * a single branch in its init logic.
     *
     * @param mv The current active Method visitor.
     * @param label The start label of the generated switch branch.
     * @param scopeNode The ScopeNode that we generate the branch for.
     */
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
            mv.visitLdcInsn(integerDeclaration.getValue());
            //add it to the scope
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/gotwo/codegen/Scope", "setLocalIntegerVariable", "(Ljava/lang/String;I)V", false);
        }

        //remove the last remaining reference to newScope from the stack
        mv.visitInsn(POP);
    }

    /**
     * Adds the run method to the class.
     * This contains the whole go2 program logic.
     *
     * @param cw The current ClassWriter instance.
     * @throws UndeclearedIdentifier
     */
    private void visitRunMethod(ClassWriter cw) throws UndeclearedIdentifier {
        int maxStackHeight = 4;
        int tempStackHeight;

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
        mv.visitCode();

        //Load the current scope to an local variable
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, "currentScope", "Lcom/gotwo/codegen/Scope;");
        mv.visitVarInsn(ASTORE, CURRENT_SCOPE);

        //Here comes the actual work
        //Convert the AST to bytecode
        //Lets do this recursive for sub-scopes
        tempStackHeight = generateScopeNodeCode(mv ,parsingResult.getRootScope() );

        if(tempStackHeight > maxStackHeight) {
            maxStackHeight = tempStackHeight;
        }

        //Return from the call
        mv.visitInsn(RETURN);
        mv.visitMaxs(maxStackHeight, 2);
        mv.visitEnd();
    }

    /**
     * Generates the code for a ScopeNode parsed from sourcecode.
     * Scopes are the most important part in an go2 program.
     * Everything happens inside a scope.
     *
     * @param mv The current MethodVisitor instance.
     * @param scopeNode The ScopeNode to generate the code for.
     * @return The maximum used stack height
     * @throws UndeclearedIdentifier
     */
    private int generateScopeNodeCode(MethodVisitor mv, ScopeNode scopeNode) throws UndeclearedIdentifier {
        int localStackHeight = 4;
        int tempStackHeight = 0;

        //Generate the current scope
        //At this point the stack should be empty
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(scopeNode.getId());
        mv.visitVarInsn(ALOAD, CURRENT_SCOPE);
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "initScopeWithId", "(ILcom/gotwo/codegen/Scope;)Lcom/gotwo/codegen/Scope;", false);
        mv.visitVarInsn(ASTORE, CURRENT_SCOPE);
        //Stack is empty again, ready to do some work


        //We simply generate one statement after another, that simple
        for(Node node : scopeNode.getChildNodes()) {
            switch (node.getType()) {
                case SCOPE:
                    tempStackHeight = generateScopeNodeCode(mv, (ScopeNode)node);
                    break;

                case ASSIGNMENT:
                    tempStackHeight = generateAssignment(mv, (AssignmentNode)node);
                    break;

                case CONDITION:
                    tempStackHeight = generateCondition(mv, (ConditionNode) node);
                    break;

                case LABEL:
                    tempStackHeight = generateLabel(mv, (LabelNode) node);
                    break;

                case GOTO:
                    tempStackHeight = generateGoTo(mv, (GoToLabelNode) node, scopeNode);
                    break;

                case GOTOSPECIAL:
                    tempStackHeight = generateGoToSpecial(mv, (GoToSpecialNode) node);
                    break;
            }

            if(tempStackHeight > localStackHeight) {
                localStackHeight = tempStackHeight;
            }
        }

        return localStackHeight;
    }

    /**
     * Generates code for specialized labels.
     * This is used for input/output right now, but
     * will be replaced with a more flexible mechanism
     * in the future.
     *
     * @param mv The current MethodVisitor instance.
     * @param labelNode The GoToSpecialNode to generate code for.
     * @return The maximum used stack height
     * @throws UndeclearedIdentifier
     */
    private int generateGoToSpecial(MethodVisitor mv, GoToSpecialNode labelNode) throws UndeclearedIdentifier {
        switch (labelNode.getSpecial()) {
            case CONSOLE:
                mv.visitVarInsn(ALOAD, CURRENT_SCOPE);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/gotwo/codegen/Scope", "printRun", "()V", false);
                return 1;
        }

        throw new UndeclearedIdentifier(labelNode.getSpeed()  + " TO " + labelNode.getSpecial());
    }

    /**
     * Generates all kinds of go to statements.
     * This takes into consideration at which speed the go
     * to should happen and generates code for the appropriate
     * scope manipulations needed.
     *
     * @param mv The current MethodVisitor instance.
     * @param labelNode The label node representing the go to statement to generate code for
     * @param currentScope The scope that contains the go to statement
     * @return The maximum used stack height
     */
    private int generateGoTo(MethodVisitor mv, GoToLabelNode labelNode, ScopeNode currentScope) {
        // Step one, find the common base scope.
        // This is the level that we need to build up on.
        // Try to be as accurate as possible at compile time,
        // so lets search the id of the target scope.
        ScopeNode currentStack = currentScope;
        ScopeNode otherStack = labelNode.getTarget().getScope();
        while(!currentStack.equals(otherStack)) { //Traverse down as long as the two scopes are not the equal base
            if(currentStack.getHeight() <= otherStack.getHeight()) {
                otherStack = otherStack.getParentScope(); // Go one down
            } else {
                currentStack = currentStack.getParentScope();
            }
        }
        //currentStack now holds our target scopeNode, so we know its id

        mv.visitVarInsn(ALOAD, CURRENT_SCOPE);
        mv.visitInsn(DUP);
        mv.visitLdcInsn(currentStack.getId()); // put the target scope id on the stack
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/gotwo/codegen/Scope", "getParentWithId","(I)Lcom/gotwo/codegen/Scope;", false);
        // Now there should be our common base scope on the stack
        mv.visitVarInsn(ASTORE, CURRENT_SCOPE); //Save it
        // The stack now holds only the "old" current scope

        if(labelNode.getTarget().getScope().getHeight() > currentStack.getHeight()) {
            generateNewScopeStack(mv, currentStack.getId(), labelNode.getTarget().getScope());
        }
        // The new current scope is now stored in CURRENT_SCOPE
        switch (labelNode.getSpeed()) {
            case GO:
                mv.visitVarInsn(ALOAD, CURRENT_SCOPE);
                mv.visitInsn(SWAP);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/gotwo/codegen/Scope", "mergeForRun","(Lcom/gotwo/codegen/Scope;)V", false);
                break;
            case RUN:
                mv.visitInsn(POP);
                break;
            case WALK:
                //TODO: Implement walk to
                break;
            case SPRINT:
                //TODO: Implement sprint to
                //Do not take any variables with us...
                //Forget about the old scope
                break;
        }


        mv.visitJumpInsn(GOTO ,labelNode.getTarget().getLabel());

        return 4;
    }

    /**
     * This method will generate a new scope based on a given target scope and an
     * base scope that we build up on.
     * Basically we need to build a stack of scopes starting at a given point in the scope hierarchy.
     *
     * @param mv The current MethodVisitor instance.
     * @param commonBaseId The integer id of the layer that the new scope stack should be build up on.
     * @param targetScope The target scope that we want to build up.
     */
    private void generateNewScopeStack(MethodVisitor mv, int commonBaseId, ScopeNode targetScope) {
        if(targetScope.getId() != commonBaseId) {
            generateNewScopeStack(mv, commonBaseId, targetScope.getParentScope()); //Go down and build our new scope from bottom to the top
        }

        //This should update the current scope one layer up a time
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(targetScope.getId());
        mv.visitVarInsn(ALOAD, CURRENT_SCOPE);
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "initScopeWithId", "(ILcom/gotwo/codegen/Scope;)Lcom/gotwo/codegen/Scope;", false);
        mv.visitVarInsn(ASTORE, CURRENT_SCOPE);
    }

    /**
     * Generates a label defined in the source code.
     * Its really simple right now, but it could get more complex later on.
     *
     * @param mv The current MethodVisitor instance.
     * @param labelNode The label to be generated
     * @return The maximum used stack height
     */
    private int generateLabel(MethodVisitor mv, LabelNode labelNode) {
        mv.visitLabel( labelNode.getLabelDeclaration().getLabel() );
        return 0;
    }

    /**
     * This will generate code for an conditional node.
     * Right now there is only a very basic if statement,
     * but this could change in the future.
     *
     * @param mv The current MethodVisitor instance.
     * @param conditionNode The Condition node to generate code for
     * @return The maximum used stack height
     * @throws UndeclearedIdentifier
     */
    private int generateCondition(MethodVisitor mv, ConditionNode conditionNode) throws UndeclearedIdentifier {
        switch (conditionNode.getBranches()) {
            case IF:
                return generateIfCondition(mv, (IfConditionNode) conditionNode);
        }

        return 0;
    }

    /**
     * Generates the code far an simple if condition.
     * This includes validation of the boolean statement and
     * generating the inner scope that is called if the
     * condition is true.
     *
     * @param mv The current MethodVisitor instance.
     * @param ifConditionNode The node to generate code for
     * @return The maximum used stack height
     * @throws UndeclearedIdentifier
     */
    private int generateIfCondition(MethodVisitor mv, IfConditionNode ifConditionNode) throws UndeclearedIdentifier {
        Label endLabel = new Label();
        int stackHeight, tempStackHeight;

        stackHeight = generateExpression(mv, ifConditionNode.getExpression(), 0);
        mv.visitJumpInsn(IFEQ, endLabel); //Go to end if it is equal to zero
        tempStackHeight = generateScopeNodeCode(mv, ifConditionNode.getIfScope()); //otherwise run a new scope...
        mv.visitLabel(endLabel);

        if(stackHeight > tempStackHeight) {
            return stackHeight;
        } else  {
            return tempStackHeight;
        }
    }

    /**
     * Generates code for an simple assignment.
     * Will evaluate the expression and set the variables value.
     *
     * @param mv The current MethodVisitor instance.
     * @param assignmentNode The node to generate code for
     * @return The maximum used stack height
     */
    private int generateAssignment(MethodVisitor mv, AssignmentNode assignmentNode) {
        //Quite simple to get an assignment done
        //Evaluate the right hand side expression(not job of this method)
        //Then simply assign the result to the right variable

        mv.visitVarInsn(ALOAD, CURRENT_SCOPE);
        mv.visitLdcInsn(assignmentNode.getIntegerDeclaration().getName());
        int stackHeight = generateExpression(mv, assignmentNode.getExpressionNode(), 0);
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
     * @param mv The current MethodVisitor instance
     * @param expressionNode The node to generate code for
     * @param stackDepth The current number of elements on the stack before the expression is evaluated
     * @return The maximum used stack height
     */
    private int generateExpression(MethodVisitor mv, ExpressionNode expressionNode, int stackDepth) {
        int localStackHeight;
        int tempStackHeight;

        if(expressionNode instanceof ExpressionNode.SubExpressionNode) {
            //An expression that has 2 sub values and an operator connecting them
            ExpressionNode.SubExpressionNode subExpressionNode = (ExpressionNode.SubExpressionNode)expressionNode;

            localStackHeight = generateExpression(mv, subExpressionNode.getLeft(), stackDepth);
            tempStackHeight = generateExpression(mv, subExpressionNode.getRight(), stackDepth + 1);

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
                case MOD:
                    mv.visitInsn(IREM);
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
            mv.visitLdcInsn(constIntExpressionNode.getValue());

            localStackHeight = stackDepth + 1;
        }

        return localStackHeight;
    }
}
