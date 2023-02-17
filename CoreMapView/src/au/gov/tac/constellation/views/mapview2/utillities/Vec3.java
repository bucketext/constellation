/*
 * Copyright 2010-2022 Australian Signals Directorate
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
package au.gov.tac.constellation.views.mapview2.utillities;

/**
 * Utility class to hold 3 variables and do vector maths on them
 *
 * @author altair1673
 */
public class Vec3 {
    private double x = 0;
    private double y = 0;
    private double z = 0;

    public Vec3(Vec3 vec3) {
        x = vec3.x;
        y = vec3.y;
        z = vec3.z;
    }

    public Vec3(double x, double y) {
        z = 0;
        this.x = x;
        this.y = y;
    }

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void multiplyFloat(float value) {
        x *= value;
        y *= value;
        z *= value;
    }

    public void multiplyDouble(double value) {
        x *= value;
        y *= value;
        z *= value;
    }

    public void addVector(Vec3 value) {
        x += value.x;
        y += value.y;
        z += value.z;
    }

    public void divVector(double d) {
        if (d != 0) {
            x /= d;
            y /= d;
            z /= d;
        }
    }

    public Vec3() {

    }

    public static double getDistance(Vec3 v1, Vec3 v2) {
        return Math.sqrt(Math.pow(v2.x - v1.x, 2) + Math.pow(v2.y - v1.y, 2));
    }

    public Vec3 cross(Vec3 v) {
        Vec3 ans = new Vec3();
        ans.x = y * v.z - z * v.y;
        ans.y = z * v.x - x * v.z;
        ans.z = x * v.y - y * v.x;

        return ans;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }


}
