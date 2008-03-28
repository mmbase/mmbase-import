package com.finalist.cmsc.tools.generator;

import java.lang.System;

import com.ice.cvsc.*;

public class JCVSUI implements CVSUserInterface {

    public void uiDisplayProgressMsg(String message) {
        System.err.println(message);
    }

    public void uiDisplayProgramError(String error) {
        System.err.println(error);
    }

    public void uiDisplayResponse(CVSResponse response) {
        System.err.println(response.getResultStatus());
    }
}
