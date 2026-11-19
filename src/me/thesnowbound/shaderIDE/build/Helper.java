package me.thesnowbound.shaderIDE.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static me.thesnowbound.shaderIDE.build.Obfuscator.*;

public record Helper(String name, String type, List<String[]> params, List<Token> expr, int end) {
    // null when an argument would have to be pasted twice and isn't a plain name or number
    String substitute(List<List<Token>> args) {
        if (args.size() != params.size()) return null;
        StringBuilder b = new StringBuilder(type).append("((");
        for (int ei = 0; ei < expr.size(); ei++) {
            Token t = expr.get(ei);
            // a name right after a dot is a field or swizzle (v.x), not the parameter that happens to be called x
            boolean member = ei > 0 && expr.get(ei - 1).text().equals(".");
            int p = -1;
            for (int k = 0; k < params.size(); k++) if (params.get(k)[1].equals(t.text())) p = k;
            if (p < 0 || !t.kind().equals("ident") || member) {
                b.append(t.text()).append(' ');
                continue;
            }
            List<Token> arg = args.get(p);
            String param = params.get(p)[1];
            long uses = 0;
            for (int k = 0; k < expr.size(); k++) {
                if (expr.get(k).text().equals(param) && !(k > 0 && expr.get(k - 1).text().equals("."))) uses++;
            }
            if (uses > 1 && !(arg.size() == 1 && !arg.get(0).kind().equals("op"))) return null;
            b.append(params.get(p)[0]).append("(").append(join(arg)).append(")");
        }
        return b.append("))").toString();
    }
}
