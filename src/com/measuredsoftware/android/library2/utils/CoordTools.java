package com.measuredsoftware.android.library2.utils;

import android.graphics.PointF;
import android.graphics.RectF;

import com.measuredsoftware.android.library2.game.Vec2;
import com.measuredsoftware.android.library2.game.Vec2F;

public class CoordTools {
  
  private static final PointF mTempPoint = new PointF();
  
  public static void calculateCorner(float angle360, int hypLen, PointF point) {
    getVelocityFromAngleAndSpeed(angle360, hypLen, point);
  }
  
  public static void calculatePivotOffset(float angle, float radius, PointF result) {
    if (radius == 0) {
      result.x = 0;
      result.y = 0;
    } else {
      CoordTools.getVelocityFromAngleAndSpeed(angle, radius, result);
    }
  }
  
  /**
   * sets the supplied PointF to offsets from centre, for the front left and right of a rectangle
   * @param hypLen
   * @param angle
   * @param angleDiff
   * @param frontLeft
   * @param frontRight
   */
  public static void calculateFrontCorners(int hypLen, float angle, float angleDiff, PointF frontLeft, PointF frontRight) {
    final float lcAngle = angle-angleDiff;
    final float rcAngle = angle+angleDiff;
    
    final float lc360Angle = lcAngle % 360;
    final float rc360Angle = rcAngle % 360;
    
    calculateCorner(lc360Angle, hypLen, frontLeft);
    calculateCorner(rc360Angle, hypLen, frontRight);
  }

  /**
   * sets the supplied PointF to offsets from centre, for the corners of a rectangle
   * @param hypLen
   * @param angle
   * @param angleDiff
   * @param frontLeft
   * @param frontRight
   * @param rearLeft
   * @param rearRight
   */
  public static void calculateCorners(int hypLen, float angle, float angleDiff, PointF frontLeft, PointF frontRight, PointF rearLeft, PointF rearRight) {
    final float lfAngle = angle-angleDiff;
    final float rfAngle = angle+angleDiff;
    
    final float lf360Angle = (lfAngle % 360);
    final float rf360Angle = (rfAngle % 360);
    
    calculateCorner(lf360Angle, hypLen, frontLeft);
    calculateCorner(rf360Angle, hypLen, frontRight);
    rearLeft.x = -frontRight.x;
    rearLeft.y = -frontRight.y;
    rearRight.x = -frontLeft.x;
    rearRight.y = -frontLeft.y;
  }

  /**
   * sets the supplied PointF to offsets from centre, for the corners of a rectangle, with different widths for front and back
   * @param hypLen
   * @param angle
   * @param frontAngleDiff
   * @param rearAngleDiff
   * @param frontLeft
   * @param frontRight
   * @param rearLeft
   * @param rearRight
   */
  public static void calculateCorners(int hypLen, float angle, float frontAngleDiff, float rearAngleDiff, PointF frontLeft, PointF frontRight, PointF rearLeft, PointF rearRight) {
    final float lfAngle = angle-frontAngleDiff;
    final float rfAngle = angle+frontAngleDiff;
    
    final float lf360Angle = (lfAngle % 360);
    final float rf360Angle = (rfAngle % 360);
    
    calculateCorner(lf360Angle, hypLen, frontLeft);
    calculateCorner(rf360Angle, hypLen, frontRight);
    
    angle += 180f;

    final float lrAngle = angle+rearAngleDiff;
    final float rrAngle = angle-rearAngleDiff;
    
    final float lr360Angle = (lrAngle % 360);
    final float rr360Angle = (rrAngle % 360);
    
    calculateCorner(lr360Angle, hypLen, rearLeft);
    calculateCorner(rr360Angle, hypLen, rearRight);
  }
  
  public static void getVelocityFromAngleAndSpeed(float angle, float speed, PointF coord) {
    final float radians = (float)Math.toRadians(angle%360);
    final float newXVel = ((float)Math.sin(radians)) * speed;
    final float newYVel = ((float)Math.cos(radians)) * speed;

    coord.x = newXVel;
    coord.y = newYVel;
  }
  
  public static void getNormalisedVelocity(float x, float y, PointF coord) {
    float hyp = (float)Math.sqrt(x*x + y*y);
    coord.x = (x / hyp);
    coord.y = (y / hyp);
  }
  
  public static void getVelocityFromAngleAndSpeed(float angle, float speed, PointF offsetCoord, PointF outCoord) {
    final float radians = (float)Math.toRadians(angle%360);
    final float newXVel = ((float)Math.sin(radians)) * speed;
    final float newYVel = ((float)Math.cos(radians)) * speed;

    outCoord.x = offsetCoord.x + newXVel;
    outCoord.y = offsetCoord.y + newYVel;
  }
  
  public static void getVelocityFromAngleAndSpeed(float angle, float speed, float offsetCoordX, float offsetCoordY, PointF outCoord) {
    final float radians = (float)Math.toRadians(angle%360);
    final float newXVel = ((float)Math.sin(radians)) * speed;
    final float newYVel = ((float)Math.cos(radians)) * speed;

    outCoord.x = offsetCoordX + newXVel;
    outCoord.y = offsetCoordY + newYVel;
  }
  
  public static void getVelocityFromAngleAndSpeed(float angle, float speed, PointF coord, boolean reverseY) {
    final float radians = (float)Math.toRadians(angle%360);
    final float newXVel = ((float)Math.sin(radians)) * speed;
    float newYVel = ((float)Math.cos(radians)) * speed;
    if (reverseY)
      newYVel = -newYVel;

    coord.x = newXVel;
    coord.y = newYVel;
  }
  
  public static float getAngleFromVelocity(float vx, float vy) {
    if (vy == 0) {
      if (vx == 0)
        return 0;
      
      return (vx > 0) ? 90 : 270;
      //////
    } else if (vx == 0) {
      return (vy > 0) ? 0 : 180;
    }
    
    final float angle = (float)Math.toDegrees((float)Math.atan(vx/vy)); 
    
    if (vy < 0.0f)
      return 180f+angle;
    else if (vx < 0.0f)
      return 360f+angle;
    
    return angle; 
  }
  
  public static float getAngleFromVelocity(PointF vel) {
    return getAngleFromVelocity(vel.x, vel.y);
  }  
  
  public static int getDotp(Vec2 va, Vec2 vb) {
    // dp = v1.vx*v2.vx + v1.vy*v2.vy;
    return ((va.x*vb.x)+(va.y*vb.y));
  }

  public static int getDotp(Vec2F va, Vec2F vb) {
    // dp = v1.vx*v2.vx + v1.vy*v2.vy;
    return (int)((va.x*vb.x)+(va.y*vb.y));
  }
  
  public static float getPerpF(float vavx, float vavy, Vec2F vb) {
    // pp = va.vx*vb.vy - va.vy*vb.vx;
    return ((vavx*vb.y) - (vavy*vb.x));
  }
  
  public static float getPerp(int vavx, int vavy, Vec2 vb) {
    // pp = va.vx*vb.vy - va.vy*vb.vx;
    return ((float)((vavx*vb.y) - (vavy*vb.x)));
  }

  public static float findIntersectionPointF(Vec2F v1, Vec2F v2, PointF intersect) {
    float t = 0.0f;
    
    float v3vx = v2.start.x-v1.start.x;
    float v3vy = v2.start.y-v1.start.y;
  
    float perpV3V2 = getPerpF(v3vx,v3vy,v2);
    float perpV1V2 = getPerpF(v1.x,v1.y,v2);
    
    if (perpV1V2 == 0.0f) {
      return 0.0f;
    }
    
    t = perpV3V2/perpV1V2;
    
    if (intersect != null) {    
      intersect.x = (v1.start.x + v1.x * t);
      intersect.y = (v1.start.y + v1.y * t);
    }
    
    return t;
  }
  
  public static float findIntersectionPoint(Vec2 v1, Vec2 v2, PointF intersect) {
    float t = 0.0f;
    
    int v3vx = v2.start.x-v1.start.x;
    int v3vy = v2.start.y-v1.start.y;
  
    float perpV3V2 = getPerp(v3vx,v3vy,v2);
    float perpV1V2 = getPerp(v1.x,v1.y,v2);
    
    if (perpV1V2 == 0.0f) {
      return 0.0f;
    }
    
    t = perpV3V2/perpV1V2;
    
    if (intersect != null) {
/*      if (t < 0.0f) {
        intersect.x = 0;
        intersect.y = 0;
      } else*/ {
        intersect.x = (v1.start.x + v1.x * t);
        intersect.y = (v1.start.y + v1.y * t);
      }    
    }
    return t;
  }
  
  
  /**
   * returns the difference between the given angle and the angle of the rect supplied
   * @param angle
   * @param rectAngle
   * @return angle difference, negative meaning it was closest to opposite, positive meaning it was closest to the rect angle supplied
   */
  public static float findClosestAngleFromOpposites(float angle, float rectAngle) {
    final float rectAngleB = rectAngle + 180f;
    
    float diff = rectAngle-angle;
    if (diff < 0f)
      diff = -diff;
    
    if (diff >= 0f && diff <= 90f) 
      return diff;
    if (diff > 270 && diff < 360f)
      return 360-diff;
    
    diff = rectAngleB - angle;
    if (diff > 180f) 
      diff -= 360;
    
    if (diff > 0f)
      diff = -diff;
    return diff;
  }

  public static float getHyp(float diffX, float diffY, boolean ignoreNegatives) {
    if (!ignoreNegatives) {
      if (diffX < 0)
        diffX = -diffX;
      if (diffY < 0)
        diffY = -diffY;
    }
    
    return (float)Math.sqrt((double)((diffX*diffX)+(diffY*diffY)));
  }
  
  public static float getHyp(PointF vec, boolean ignoreNegatives) {
    return getHyp(vec.x, vec.y, ignoreNegatives);
  }
  
  public static void calcDrawOffsetGLRotate(float angle, float hypLen, PointF outputPoint) {
    angle+=45;
    if (angle > 359)
      angle = angle % 360;
    
    CoordTools.getVelocityFromAngleAndSpeed(angle, hypLen, outputPoint);
    outputPoint.x = Math.round(-outputPoint.x);
    outputPoint.y = Math.round(-outputPoint.y);
  }
  
  public static void getNormalLeft(PointF in, PointF out) {
    out.x = in.y;
    out.y = -in.x;
  }

  public static void getNormalLeft(PointF inOut) {
    float temp = inOut.x;
    inOut.x = inOut.y;
    inOut.y = -temp;
  }

  public static void getNormalRight(PointF in, PointF out) {
    out.x = -in.y;
    out.y = in.x;
  }

  public static void getNormalRight(PointF inOut) {
    float temp = inOut.x;
    inOut.x = -inOut.y;
    inOut.y = temp;
  }
  
  public static float getNormalAngle(float inAngle, boolean leftRight) {
    // left?
    if (!leftRight) {
      inAngle -= 90;
      if (inAngle < 0)
        inAngle += 360f;
    } else {
      inAngle += 90;
      inAngle %= 360f;
    }
    
    return inAngle;
  }
  
  public static boolean objFacingRight(float objAngle, float diffVecX, float diffVecY) {
    final float diffAngle = (CoordTools.getAngleFromVelocity(diffVecX, diffVecY)) + 360f;
    objAngle += 360f;
    
    return (objAngle < diffAngle);
  }

  private static final PointF tp1 = new PointF();
  
  public static boolean objFacingRight(float targetVecX, float targetVecY, float targetObjVecX, float targetObjVecY) {
    // get right normal
    tp1.x = targetObjVecX;
    tp1.y = targetObjVecY;
    
    CoordTools.getNormalRight(tp1);
    
    return CoordTools.sameDirection(targetVecX, targetVecY, tp1.x, tp1.y);
  }

  private static final PointF mTempVec = new PointF();
  public static void calcOrbitVector(final boolean leftRight, PointF gravVec, float mass, float distanceBetween, PointF orbitVec) {
    // get normal of gravity
    if (!leftRight)
      CoordTools.getNormalRight(gravVec, mTempVec);
    else
      CoordTools.getNormalLeft(gravVec, mTempVec);
    
    final float v = (float)(ValueTools.GRAVITATIONAL_CONSTANT*mass)/distanceBetween;
    final float angle = CoordTools.getAngleFromVelocity(mTempVec.x, mTempVec.y);
    CoordTools.getVelocityFromAngleAndSpeed(angle, v, orbitVec);
  } 
  
  public static void calcGravity(final long timeInc, final float vecX, final float vecY, final float gravI, final float radius, PointF outVel) {
    CoordTools.getNormalisedVelocity(vecX, vecY, outVel);
    final float distance = CoordTools.getHyp(vecX, vecY, false);
    // get the x in I/x (where I is gravity at surface of object)
    final float squared = (float)Math.pow((distance/radius),2f);
    
    // get inverse
    final float intensity = (gravI/squared)*timeInc;
    
    // apply gravity to player's vector
    outVel.x *= intensity;
    outVel.y *= intensity;
  }
  
  // extra so that calling code doesn't also have to calc distance
  public static void calcGravity(final float timeInc, final float vecX, final float vecY, final float distance, final float gravI, final float radius, PointF outVel) {
    CoordTools.getNormalisedVelocity(vecX, vecY, outVel);
    // get the x in I/x (where I is gravity at surface of object)
    final float squared = (float)Math.pow((distance/radius),2f);
    
    // get inverse
    final float intensity = (gravI/squared)*timeInc;
    
    // apply gravity to player's vector
    outVel.x *= intensity;
    outVel.y *= intensity;
  }

  public static float getGravI(float mass, float radius) {
    return (float)(mass/ (4*(Math.PI*( Math.pow(radius,2) ) ) ) );
  }

  public static boolean sameDirection(float ax, float ay, float bx, float by) {
    return ( ((ax*bx) + (ay*by)) > 0);
  }

  public static boolean pointsWithin(float ax, float ay, float bx, float by, float range) {
    final float dx = ValueTools.makePositive(ax-bx);
    final float dy = ValueTools.makePositive(ay-by);
    
    return (CoordTools.getHyp(dx, dy, false) < range);
  }
  
  public static float distanceBetween(float ax, float ay, float bx, float by) {
    final float dx = ValueTools.makePositive(ax-bx);
    final float dy = ValueTools.makePositive(ay-by);
    
    return CoordTools.getHyp(dx, dy, false);    
  }

  /**
   * determines whether comparison is between current-bounds AND current+bounds (inclusive)
   * @param current
   * @param comparison
   * @param bounds
   * @return
   */
  public static boolean withinAngle(float current, float comparison, float bounds) {

    float diff180 = 180-current;
    if (diff180 > 90 || diff180 < -90) {
      current = (current+diff180);
      comparison = ValueTools.makePositive((comparison+diff180)%360f);
    }
    final float lower = current-bounds;
    final float upper = current+bounds;
    
    return (comparison >= lower && comparison <= upper);
  }  
  
  /**
   * determines whether comparison is between current-bounds AND current+bounds (inclusive)
   * @param current
   * @param comparison
   * @param bounds
   * @return 0 if within, negative if less, positive if more
   */
  public static int withinAngleInt(float current, float comparison, float bounds) {

    float diff180 = 180-current;
    if (diff180 > 90 || diff180 < -90) {
      current = (current+diff180);
      comparison = ValueTools.makePositive((comparison+diff180)%360f);
    }
    final float lower = current-bounds;
    final float upper = current+bounds;
    
    if (comparison < lower)
      return -1;
    if (comparison > upper)
      return 1;

    return 0;
  }  
  
  /**
   * determines whether comparison is between current-bounds AND current+bounds (inclusive)
   * @param current
   * @param comparison
   * @param bounds
   * @return value - factor of how close to comparison, negative if less than, positive if greater than
   */
  public static float withinAngleFloat(float current, float comparison, float bounds) {

    float diff180 = 180-current;
    if (diff180 > 90 || diff180 < -90) {
      current = (current+diff180);
      comparison = ValueTools.makePositive((comparison+diff180)%360f);
    }
    final float lower = current-bounds;
    final float upper = current+bounds;
    
    return (comparison-current)/bounds;
  }  
  
  public static float getAngleDifferenceAbs(float angleA, float angleB) {
    //return Math.abs((angleA + 180 - angleB) % 360 - 180);
    return Math.abs(getAngleDifference(angleA, angleB));
  }

  public static float getAngleDifference(float angleA, float angleB) {
    float difference = angleA - angleB;
    while (difference < -180) difference += 360;
    while (difference > 180) difference -= 360;
    return difference;
  }
  
  public static float calcAngleFromTilt(float startX, float startY, float tiltX, float tiltY, float dzX, float dzY) {
    float diffX = 0;
    float diffY = 0;

    {
      final float dzXPos = startX+dzX; 
      final float dzXNeg = startX-dzX; 
      // x first, outside of deadzone?
      if (tiltX > dzXPos) {
        diffX = tiltX - dzXPos;
      } else if (tiltX < dzXNeg) {
        diffX = tiltX - dzXNeg;
      }
    }
    
    final float dzYPos = startY+dzY; 
    final float dzYNeg = startY-dzY; 
    // x first, outside of deadzone?
    if (tiltY > dzYPos) {
      diffY = tiltY - dzYPos;
    } else if (tiltY < dzYNeg) {
      diffY = tiltY - dzYNeg;
    }
    
    //Log.d("wh","dx: " + diffX + ", dy: " + diffY);

    return CoordTools.getAngleFromVelocity(-diffX, diffY);
  }

  public static void setRect(float px, float py, float longestSideHalfLen, RectF rect) {
    rect.left   = px-longestSideHalfLen;
    rect.right  = px+longestSideHalfLen;
    rect.top    = py-longestSideHalfLen;
    rect.bottom = py+longestSideHalfLen;
  }

  public static boolean withinDistance(int ax, int ay, float bx, float by, float withinDistance) {
    final float distance = CoordTools.getHyp(ax-bx, ay-by, false);
    return (distance < withinDistance);
  }

  /**
   * returns whether the velocity is zero or not
   * @param vel the velocity to check
   * @return 
   */
  public static boolean noVelocity(PointF vel) {
    return (vel.x == 0 && vel.y == 0);
  }

  public static boolean workoutDirection(PointF velocity, float distX, float distY) {
    // get left of ship's vel
    CoordTools.getNormalLeft(velocity, mTempPoint);
    
    // if left is the same direction, it's anti-clockwise, otherwise it's clockwise
    return (CoordTools.sameDirection(mTempPoint.x, mTempPoint.y, distX, distY));
  }
}
