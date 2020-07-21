/*
 * Copyright 2010-2020 Australian Signals Directorate
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
package au.gov.asd.tac.constellation.visual.vulkan;

import static au.gov.asd.tac.constellation.visual.vulkan.utils.CVKUtils.CVKLOGGER;
import static au.gov.asd.tac.constellation.visual.vulkan.utils.CVKUtils.VkSucceeded;
import static au.gov.asd.tac.constellation.visual.vulkan.utils.CVKUtils.checkVKret;
import java.nio.LongBuffer;
import java.util.logging.Level;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.system.NativeType;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.VK10.VK_API_VERSION_1_0;
import static org.lwjgl.vulkan.VK10.VK_FALSE;
import static org.lwjgl.vulkan.VK10.VK_MAKE_VERSION;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateInstance;
import static org.lwjgl.vulkan.VK10.vkDestroyInstance;
import static org.lwjgl.vulkan.VK10.vkGetInstanceProcAddr;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCreateInfoEXT;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

public class CVKInstance {
    
    private VkInstance cvkInstance = null;
    protected long hDebugMessengerHandle = VK_NULL_HANDLE;
    
    
    public VkInstance GetInstance() { return cvkInstance; }
    
    
    public void Deinit() {
        vkDestroyInstance(cvkInstance, null);
    }

   /**
     * Initialises the VkInstance, the object that represents a Vulkan API
     * instantiation.
     * <p>
     * VkInstance is the LWJGL object that wraps the native cvkInstance handle.
     * In OpenGL state is global, in Vulkan state is stored in the VkInstance
     * object. The create info struct is used internally to create a
     *  VKCapabilitiesInstance. We need this because reasons.
     *
     * @param stack
     * @param pbExtensions
     * @param pbValidationLayers
     * @param debugging
     * @return 
     */
    @NativeType("VkResult")
    public int Init(MemoryStack stack, PointerBuffer pbExtensions, PointerBuffer pbValidationLayers, boolean debugging) {
        // Create the application info struct.  This is a sub struct of the createinfo struct below
        VkApplicationInfo appInfo = VkApplicationInfo.mallocStack(stack)
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName(stack.UTF8("Constellation"))
                .applicationVersion(VK_MAKE_VERSION(1, 0, 0))
                .pEngineName(stack.UTF8("NONE"))
                .apiVersion(VK_API_VERSION_1_0);  //Highest version of Vulkan supported by Constellation
        
        // Create the CreateInfo struct
        VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.mallocStack()
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pNext(0L)
                .pApplicationInfo(appInfo)
                .ppEnabledExtensionNames(pbExtensions)
                .ppEnabledLayerNames(pbValidationLayers);
        
        // Debug create struct if needed
        VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = null;
        if (debugging) {
            debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.callocStack(stack);
            debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
            debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
            debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
            debugCreateInfo.pfnUserCallback(CVKInstance::DebugCallback);
            pCreateInfo.pNext(debugCreateInfo.address());
        }        

        // Create the native VkInstance and return a pointer to it's handle in pInstance
        PointerBuffer pInstance = stack.pointers(1);
        int ret = vkCreateInstance(pCreateInfo, null, pInstance);
        if (VkSucceeded(ret)) {
            long instance = pInstance.get(0);
            cvkInstance = new VkInstance(instance, pCreateInfo);
            
            // If the function exists, register a validation layer callback
            if (debugging) {
                assert(debugCreateInfo != null);
                if (vkGetInstanceProcAddr(cvkInstance, "vkCreateDebugUtilsMessengerEXT") != NULL) {
                    LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);
                    checkVKret(vkCreateDebugUtilsMessengerEXT(cvkInstance, debugCreateInfo, null, pDebugMessenger));
                    hDebugMessengerHandle = pDebugMessenger.get(0);
                }  
            }
        }
        
        return ret;
    } 
    
    
    /**
     *
     * API specifies this function MUST return VK_FALSE
     * 
     * @param messageSeverity
     * @param messageType
     * @param pCallbackData
     * @param pUserData
     * @return
     */
    public static int DebugCallback(int messageSeverity, int messageType, long pCallbackData, long pUserData) {
        Level level;
        switch (messageSeverity) {
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT: level = Level.SEVERE; break;
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT: level = Level.WARNING; break;
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT:
            case VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT:
            default:
                level = Level.INFO; break;
        }                      
        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
        String callbackMsg = callbackData.pMessageString();
        CVKLOGGER.log(level, "Validation layer: {0}", callbackMsg);
        
        // This is an E for effort attempt to get something logged in the case
        // that Constellation/JVM is crashing to desktop.
        for (var handler : CVKLOGGER.getHandlers()) {
            handler.flush();
        }
        
        // DELETE AFTER DEBUGGING
//        boolean isCmdBuffer = false;
//        if (callbackMsg.contains("type = VK_OBJECT_TYPE_COMMAND_BUFFER")) {
//            isCmdBuffer = true;
//        } else {
//            isCmdBuffer = false;
//        }
        
        // TODO_TT: this has to be false because...
        return VK_FALSE;
    }                
}
