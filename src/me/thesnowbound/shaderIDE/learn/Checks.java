package me.thesnowbound.shaderIDE.learn;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// the small checks that lesson tasks are written with: has, count, uses, lacks and step
public final class Checks {
    private Checks() {
    }

    // the checks only look at code, so a task can't be "done" by writing the answer in a comment
    public static String code(String src) {
        return src.replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("//[^\\n]*", "");
    }

    public static Predicate<String> has(String regex) {
        Pattern p = Pattern.compile(regex);
        return src -> p.matcher(code(src)).find();
    }

    public static Predicate<String> count(String regex, int atLeast) {
        Pattern p = Pattern.compile(regex);
        return src -> {
            Matcher m = p.matcher(code(src));
            int n = 0;
            while (m.find()) n++;
            return n >= atLeast;
        };
    }

    // the parameter itself doesn't count, the task wants the name used somewhere in the code
    public static Predicate<String> uses(String name) {
        Pattern declared = Pattern.compile("\\bfloat\\s+" + name + "\\b");
        Pattern used = Pattern.compile("\\b" + name + "\\b");
        return src -> used.matcher(declared.matcher(code(src)).replaceAll("")).find();
    }

    public static Predicate<String> lacks(String text) {
        return src -> !code(src).contains(text);
    }

    public static Step step(String task, String hint, String example, Predicate<String> done) {
        return new Step(task, hint, example, done);
    }

}
