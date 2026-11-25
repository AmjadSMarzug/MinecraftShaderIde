package me.thesnowbound.shaderIDE.learn;

import me.thesnowbound.shaderIDE.learn.lessons.LessonBasics;
import me.thesnowbound.shaderIDE.learn.lessons.LessonClouds;
import me.thesnowbound.shaderIDE.learn.lessons.LessonFirstSky;
import me.thesnowbound.shaderIDE.learn.lessons.LessonGlow;
import me.thesnowbound.shaderIDE.learn.lessons.LessonLighting;
import me.thesnowbound.shaderIDE.learn.lessons.LessonMaterial;
import me.thesnowbound.shaderIDE.learn.lessons.LessonObject;
import me.thesnowbound.shaderIDE.learn.lessons.LessonOverlay;
import me.thesnowbound.shaderIDE.learn.lessons.LessonText;
import me.thesnowbound.shaderIDE.learn.lessons.LessonPalettes;
import me.thesnowbound.shaderIDE.learn.lessons.LessonShapes;
import me.thesnowbound.shaderIDE.learn.lessons.LessonStars;
import me.thesnowbound.shaderIDE.learn.lessons.LessonSun;
import me.thesnowbound.shaderIDE.learn.lessons.LessonSurface;
import me.thesnowbound.shaderIDE.learn.lessons.LessonWarp;

import java.util.List;
import java.util.Map;

// the lessons in order. each lesson lives in its own file under lessons/
public final class Lessons {
    private Lessons() {
    }

    static final List<Lesson> LESSONS = List.of(
            LessonBasics.LESSON,
            LessonFirstSky.LESSON,
            LessonSun.LESSON,
            LessonClouds.LESSON,
            LessonStars.LESSON,
            LessonWarp.LESSON,
            LessonPalettes.LESSON,
            LessonObject.LESSON,
            LessonShapes.LESSON,
            LessonLighting.LESSON,
            LessonGlow.LESSON,
            LessonSurface.LESSON,
            LessonMaterial.LESSON,
            LessonOverlay.LESSON,
            LessonText.LESSON);

    // one open task per lesson with no example, where you have to work it out yourself
    public static final java.util.Map<String, String> CHALLENGES = java.util.Map.ofEntries(
            java.util.Map.entry("lesson_basics", "Make the sky go from one color at the bottom to another at the top using only +, * and variables, no mix."),
            java.util.Map.entry("lesson_first_sky", "Add a warm band of light just above the horizon that fades out by the time dir.y reaches 0.3."),
            java.util.Map.entry("lesson_sun", "Put a moon on the opposite side of the sky from the sun, smaller and pale blue. What direction is opposite sunDir?"),
            java.util.Map.entry("lesson_clouds", "Make the clouds darker underneath. One way: look the noise up a second time a little higher and use the difference."),
            java.util.Map.entry("lesson_stars", "Hide the stars near the horizon, and make a rare few of them much bigger and brighter than the rest."),
            java.util.Map.entry("lesson_warp", "Warp the warp: build a second offset from the already warped position and feed that in too."),
            java.util.Map.entry("lesson_palettes", "Go back to the clouds or the stars and color them with your palette instead of hand picked colors."),
            java.util.Map.entry("lesson_object", "Make the sphere squash as it lands and stretch as it rises, by scaling p.y with time before measuring the distance."),
            java.util.Map.entry("lesson_shapes", "Build something real out of spheres, boxes and smooth blends: a snowman, a mushroom or a lamp."),
            java.util.Map.entry("lesson_lighting", "Add a second, colored light from below, like sunlight bouncing up off the ground."),
            java.util.Map.entry("lesson_glow", "Add a small spark that circles the core. cos(time) and sin(time) make a point go round in a circle."),
            java.util.Map.entry("lesson_surface", "Switch the shape to floor and draw rings that keep spreading out from the middle, like ripples on water."),
            java.util.Map.entry("lesson_material", "Make the color depend on how high the pixel is, so the bottom of the item burns hot and the top cools down."),
            java.util.Map.entry("lesson_overlay", "Make frost: white, see-through noise that only shows near the edges and creeps further in over the first few seconds."));

}
