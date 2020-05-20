/*
 * Copyright 2010-2019 Australian Signals Directorate
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
package au.gov.asd.tac.constellation.visual.opengl.utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL30;

/**
 * Encapsulate a float buffer.
 *
 * @author algol
 */
public class FloatTextureBuffer extends TextureBuffer<FloatBuffer> {

    public FloatTextureBuffer(final GL30 gl, final FloatBuffer buffer) {
        super(gl, buffer);
    }

    @Override
    protected int sizeOfType() {
        return Float.BYTES;
    }

    @Override
    protected int internalFormat() {
        return GL30.GL_RGBA32F;
    }

    @Override
    public FloatBuffer connectBuffer(GL30 gl) {
        // TODO_TT:
        return null;
//        gl.glBindBuffer(GL30.GL_TEXTURE_BUFFER, getBufferName());
//        ByteBuffer buffer = gl.glMapBuffer(GL30.GL_TEXTURE_BUFFER, GL30.GL_READ_WRITE);
//        return buffer.order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

}
