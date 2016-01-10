package com.gotwo.codegen;

/**
 * Created by florian on 10/01/16.
 */
public class IO extends GoTwoBase{

    @Override
    public void run() {
        String[] callInfo = targetLabel.split(" ");
        if(callInfo[0].equalsIgnoreCase("print")) {
            if(callInfo.length == 2) {
                int val = currentScope.getIntegerValue(callInfo[1]);
                System.out.print(val);
            }
        }
        if(callInfo[0].equalsIgnoreCase("println")) {
            if(callInfo.length == 2) {
                int val = currentScope.getIntegerValue(callInfo[1]);
                System.out.println(val);
            }
        }

        goBack();
        return;
    }

}
