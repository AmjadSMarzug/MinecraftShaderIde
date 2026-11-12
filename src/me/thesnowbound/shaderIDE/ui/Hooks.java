package me.thesnowbound.shaderIDE.ui;

import me.thesnowbound.shaderIDE.learn.Lesson;
import me.thesnowbound.shaderIDE.presets.Example;
import me.thesnowbound.shaderIDE.project.Project;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static me.thesnowbound.shaderIDE.ui.Home.*;

public record Hooks(Runnable newProject, Runnable openProject, Consumer<Path> openPath, Runnable newEffect, Runnable build,
             Consumer<Example> example, Consumer<Lesson> lesson, Runnable back, Runnable updates,
             Runnable releases, Supplier<Project> project, Supplier<Boolean> editorOpen, Supplier<List<String>> recent,
             JCheckBoxMenuItem obfuscate, JCheckBoxMenuItem check, JCheckBoxMenuItem skript, Consumer<Theme> theme,
             java.util.function.BiConsumer<Path, Path> openEffect) {
}
