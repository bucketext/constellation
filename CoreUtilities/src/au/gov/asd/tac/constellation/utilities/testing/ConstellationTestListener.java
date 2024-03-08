/*
* Copyright 2010-2023 Australian Signals Directorate
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
 */
package au.gov.asd.tac.constellation.utilities.testing;

import au.gov.asd.tac.constellation.utilities.file.autosave.AutosaveUtilities;
import java.awt.GraphicsEnvironment;
import java.io.File;
import org.testfx.api.FxToolkit;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * A Test Listener that is able to simplify test creation and test debugging.
 * A class that can be used to debug testing environments
 * @author capricornunicorn123
 */
public class ConstellationTestListener implements ITestListener {

    //Called prior to test start up.
    @Override
    public void onTestStart(ITestResult arg0) {
        System.out.println(String.format("ON TEST START onTestStart of test: %s, Environment is headless: %s", arg0.getName(), GraphicsEnvironment.isHeadless()));
    }

    @Override
    public void onTestSuccess(ITestResult arg0) {
                System.out.println(String.format("onTestSuccess, Environment is headless: %s", GraphicsEnvironment.isHeadless()));
        //
    }

    @Override
    public void onTestFailure(ITestResult arg0) {
        System.out.println(String.format("onTestFailure, Environment is headless: %s", GraphicsEnvironment.isHeadless()));
        //
    }

    @Override
    public void onTestSkipped(ITestResult arg0) {
        //
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
        //
    }

    @Override
    public void onStart(ITestContext arg0) {
        
        System.out.println(String.format("ON START of %s, Environment is Headless: %s", arg0.getCurrentXmlTest().getClasses().stream().findFirst().get().getName(), GraphicsEnvironment.isHeadless()));
        
        System.out.println(String.format("ON START of %s, FX Application Thread is Running: %s", arg0.getCurrentXmlTest().getClasses().stream().findFirst().get().getName(), FxToolkit.isFXApplicationThreadRunning()));
        
        File dir = AutosaveUtilities.getAutosaveDir();
        System.out.println(String.format("ON START of %s, Austosave File Count: %s", arg0.getCurrentXmlTest().getClasses().stream().findFirst().get().getName(), dir.length()));
        if (dir != null){
            File[] files = dir.listFiles();
            if (files.length > 0){
                System.out.println(String.format("Files Found in Dir: %s", dir.getAbsolutePath()));
                for (File file : files){
                    System.out.println(file.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public void onFinish(ITestContext arg0) {
        System.out.println(String.format("ON FINISH of %s, Environment is Headless: %s", arg0.getCurrentXmlTest().getClasses().stream().findFirst().get().getName(), GraphicsEnvironment.isHeadless()));
        
        System.out.println(String.format("ON FINISH of %s, FX Application Thread is Running: %s", arg0.getCurrentXmlTest().getClasses().stream().findFirst().get().getName(), FxToolkit.isFXApplicationThreadRunning()));
        
        File dir = AutosaveUtilities.getAutosaveDir();
        System.out.println(String.format("ON FINISH of %s, Austosave File Count: %s", arg0.getCurrentXmlTest().getClasses().stream().findFirst().get().getName(), dir.length()));
        if (dir != null){
            File[] files = dir.listFiles();
            if (files.length > 0){
                System.out.println(String.format("Files Found in Dir: %s", dir.getAbsolutePath()));
                for (File file : files){
                    System.out.println(file.getAbsolutePath());
                }
            }
        }
    }
    
}
