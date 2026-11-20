package me.thesnowbound.shaderIDE.build;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// the item model for a sky or an object: a cube, a cylinder or a flat circle. the game's item models only hold
// cuboids, so the round shapes are built from them. each piece only gets the faces on its outside, and every model
// is checked so no two faces sit in the same place, since those flicker against each other in game
final class Models {
    static final double THICK = 0.2;   // thickness of the thin pieces, in model pixels
    static final int WALLS = 16;       // sides of the cylinder
    static final int CAP_GRID = 6;     // tiles across each lid of the cylinder
    static final int DISC_GRID = 8;    // tiles across the circle
    static final Map<String, Object> UV = Map.of("uv", List.of(0.5, 0.5, 15.5, 15.5), "texture", "#sky");

    private Models() {
    }

    static Map<String, Object> sky(String shape, String ref, float[] box) {
        Map<String, Object> model;
        if (shape.equals("cube")) {
            model = Forge.cubeModel(ref, box);
        } else {
            double radius = Math.max(1.0, (box[0] + box[2]) / 2 * 8);
            double half = Math.max(1.0, box[1] * 8);
            List<Map<String, Object>> elements = new ArrayList<>();
            if (shape.equals("cylinder")) {
                double apothem = radius * Math.cos(Math.PI / WALLS);
                double width = 2 * radius * Math.sin(Math.PI / WALLS);
                for (int i = 0; i < WALLS; i++) elements.add(wall(2 * Math.PI * i / WALLS, apothem, width, half));
                elements.addAll(tiles(radius, 8 + half, CAP_GRID, Set.of("up")));
                elements.addAll(tiles(radius, 8 - half, CAP_GRID, Set.of("down")));
            } else if (shape.equals("circle")) {
                elements.addAll(tiles(radius, 8, DISC_GRID, Set.of("up", "down")));
            } else {
                throw new IllegalArgumentException("unknown model \"" + shape + "\"");
            }
            model = new LinkedHashMap<>();
            model.put("textures", Map.of("sky", ref, "particle", ref));
            model.put("elements", elements);
            double widest = Math.max(box[0], Math.max(box[1], box[2]));
            if (widest > 1f) {
                // same fit as the cube, so a scaled up item still fits its inventory slot
                double shrink = Math.round(1000.0 / widest) / 1000.0;
                Map<String, Object> fit = Map.of("scale", List.of(shrink, shrink, shrink));
                model.put("display", Map.of("gui", fit, "ground", fit, "fixed", fit));
            }
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> elements = (List<Map<String, Object>>) model.get("elements");
        checkOverlap(elements, shape);
        return model;
    }

    // one wall piece of the cylinder, standing at angle phi around the middle. the piece turns so its outside
    // face faces away from the middle, and the turn stays within 45 degrees, the most a game model allows
    static Map<String, Object> wall(double phi, double apothem, double width, double half) {
        double ox = Math.cos(phi), oz = Math.sin(phi);
        String face = null;
        double turn = 0;
        for (String f : List.of("south", "east", "north", "west")) {
            double[] n = normal(f);
            double t = Math.atan2(n[2], n[0]) - phi;
            t = Math.atan2(Math.sin(t), Math.cos(t));
            if (Math.abs(t) <= Math.PI / 4 + 1e-9) {
                face = f;
                turn = t;
                break;
            }
        }
        double cx = 8 + (apothem - THICK / 2) * ox, cz = 8 + (apothem - THICK / 2) * oz;
        boolean alongZ = face.equals("south") || face.equals("north");
        double hx = alongZ ? width / 2 : THICK / 2, hz = alongZ ? THICK / 2 : width / 2;
        Map<String, Object> rotation = new LinkedHashMap<>();
        rotation.put("origin", List.of(r(cx), 8.0, r(cz)));
        rotation.put("axis", "y");
        rotation.put("angle", r(Math.toDegrees(turn)));
        Map<String, Object> faces = new LinkedHashMap<>();
        faces.put(face, UV);
        return element(List.of(r(cx - hx), r(8 - half), r(cz - hz)), List.of(r(cx + hx), r(8 + half), r(cz + hz)), rotation, faces);
    }

    // a flat grid of tiles inside a circle. neighbours share edges but never overlap
    static List<Map<String, Object>> tiles(double radius, double y, int grid, Set<String> sides) {
        List<Map<String, Object>> out = new ArrayList<>();
        double s = 2 * radius / grid;
        for (int i = 0; i < grid; i++) {
            for (int j = 0; j < grid; j++) {
                double x0 = 8 - radius + i * s, z0 = 8 - radius + j * s;
                double cx = x0 + s / 2 - 8, cz = z0 + s / 2 - 8;
                if (cx * cx + cz * cz > radius * radius) continue;
                Map<String, Object> faces = new LinkedHashMap<>();
                for (String side : sides) faces.put(side, UV);
                out.add(element(List.of(r(x0), r(y - THICK / 2), r(z0)), List.of(r(x0 + s), r(y + THICK / 2), r(z0 + s)), null, faces));
            }
        }
        return out;
    }

    private static Map<String, Object> element(List<Double> from, List<Double> to, Map<String, Object> rotation, Map<String, Object> faces) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("from", from);
        e.put("to", to);
        if (rotation != null) e.put("rotation", rotation);
        e.put("shade", false);
        e.put("faces", faces);
        return e;
    }

    private static double r(double v) {
        return Math.round(v * 10000) / 10000.0;
    }

    // the outside direction of each face before any turn: x, y, z
    static double[] normal(String face) {
        return switch (face) {
            case "north" -> new double[]{0, 0, -1};
            case "south" -> new double[]{0, 0, 1};
            case "east" -> new double[]{1, 0, 0};
            case "west" -> new double[]{-1, 0, 0};
            case "up" -> new double[]{0, 1, 0};
            case "down" -> new double[]{0, -1, 0};
            default -> throw new IllegalArgumentException("unknown face " + face);
        };
    }

    // ---- overlap check ----

    // a face in world space: its outward normal, a point on its plane, and its four corners in order
    record Face(String name, int element, double[] normal, double[][] corners) {
        double offset() {
            return dot(normal, corners[0]);
        }
    }

    static Face face(String name, int element, double[] from, double[] to, double[] origin, double angle) {
        double fx = from[0], fy = from[1], fz = from[2], tx = to[0], ty = to[1], tz = to[2];
        double[][] c = switch (name) {
            case "north" -> new double[][]{{fx, fy, fz}, {tx, fy, fz}, {tx, ty, fz}, {fx, ty, fz}};
            case "south" -> new double[][]{{fx, fy, tz}, {tx, fy, tz}, {tx, ty, tz}, {fx, ty, tz}};
            case "west" -> new double[][]{{fx, fy, fz}, {fx, fy, tz}, {fx, ty, tz}, {fx, ty, fz}};
            case "east" -> new double[][]{{tx, fy, fz}, {tx, fy, tz}, {tx, ty, tz}, {tx, ty, fz}};
            case "down" -> new double[][]{{fx, fy, fz}, {tx, fy, fz}, {tx, fy, tz}, {fx, fy, tz}};
            case "up" -> new double[][]{{fx, ty, fz}, {tx, ty, fz}, {tx, ty, tz}, {fx, ty, tz}};
            default -> throw new IllegalArgumentException("unknown face " + name);
        };
        double[] n = normal(name);
        if (origin != null) {
            for (int k = 0; k < 4; k++) c[k] = add(origin, turnY(sub(c[k], origin), angle));
            n = turnY(n, angle);
        }
        return new Face(name, element, n, c);
    }

    // throws if two faces lie in the same plane and overlap on it
    static void checkOverlap(List<Map<String, Object>> elements, String what) {
        List<Face> faces = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            Map<?, ?> e = elements.get(i);
            double[] from = vec(e.get("from")), to = vec(e.get("to"));
            double[] origin = null;
            double angle = 0;
            if (e.get("rotation") instanceof Map<?, ?> rot) {
                origin = vec(rot.get("origin"));
                angle = Math.toRadians(((Number) rot.get("angle")).doubleValue());
            }
            for (Object name : ((Map<?, ?>) e.get("faces")).keySet()) {
                faces.add(face(String.valueOf(name), i, from, to, origin, angle));
            }
        }
        for (int a = 0; a < faces.size(); a++) {
            for (int b = a + 1; b < faces.size(); b++) {
                Face fa = faces.get(a), fb = faces.get(b);
                if (Math.abs(dot(fa.normal, fb.normal)) < 0.999) continue;
                if (Math.abs(dot(fa.normal, fb.corners[0]) - fa.offset()) > 1e-4) continue;
                double area = overlapArea(fa, fb);
                if (area > 1e-6) {
                    throw new IllegalStateException(what + " model: faces overlap, element " + fa.element() + " " + fa.name()
                            + " and element " + fb.element() + " " + fb.name() + " share " + String.format("%.4f", area) + " square pixels");
                }
            }
        }
    }

    // the shared area of two faces on the same plane, found by clipping one quad against the other
    static double overlapArea(Face a, Face b) {
        double[] u = unit(sub(a.corners[1], a.corners[0]));
        double[] v = unit(cross(a.normal, u));
        List<double[]> pa = flat(a.corners, a.corners[0], u, v), pb = flat(b.corners, a.corners[0], u, v);
        if (signedArea(pa) < 0) java.util.Collections.reverse(pa);
        if (signedArea(pb) < 0) java.util.Collections.reverse(pb);
        return Math.abs(signedArea(clip(pb, pa)));
    }

    private static List<double[]> flat(double[][] c, double[] origin, double[] u, double[] v) {
        List<double[]> out = new ArrayList<>();
        for (double[] p : c) {
            double[] d = sub(p, origin);
            out.add(new double[]{dot(d, u), dot(d, v)});
        }
        return out;
    }

    private static List<double[]> clip(List<double[]> subject, List<double[]> clipper) {
        List<double[]> out = subject;
        for (int k = 0; k < clipper.size() && !out.isEmpty(); k++) {
            double[] a = clipper.get(k), b = clipper.get((k + 1) % clipper.size());
            List<double[]> in = out;
            out = new ArrayList<>();
            double[] prev = in.get(in.size() - 1);
            for (double[] cur : in) {
                double sc = side(a, b, cur), sp = side(a, b, prev);
                if (sc >= 0) {
                    if (sp < 0) out.add(cross2(prev, cur, a, b));
                    out.add(cur);
                } else if (sp >= 0) {
                    out.add(cross2(prev, cur, a, b));
                }
                prev = cur;
            }
        }
        return out;
    }

    private static double side(double[] a, double[] b, double[] p) {
        return (b[0] - a[0]) * (p[1] - a[1]) - (b[1] - a[1]) * (p[0] - a[0]);
    }

    private static double[] cross2(double[] p, double[] q, double[] a, double[] b) {
        double sp = side(a, b, p), sq = side(a, b, q);
        double t = sp / (sp - sq);
        return new double[]{p[0] + t * (q[0] - p[0]), p[1] + t * (q[1] - p[1])};
    }

    private static double signedArea(List<double[]> poly) {
        double s = 0;
        for (int i = 0; i < poly.size(); i++) {
            double[] p = poly.get(i), q = poly.get((i + 1) % poly.size());
            s += p[0] * q[1] - q[0] * p[1];
        }
        return s / 2;
    }

    // turns a vector about the y axis, the same way the game turns a model element
    static double[] turnY(double[] v, double angle) {
        double c = Math.cos(angle), s = Math.sin(angle);
        return new double[]{v[0] * c + v[2] * s, v[1], -v[0] * s + v[2] * c};
    }

    private static double[] vec(Object o) {
        List<?> l = (List<?>) o;
        return new double[]{((Number) l.get(0)).doubleValue(), ((Number) l.get(1)).doubleValue(), ((Number) l.get(2)).doubleValue()};
    }

    private static double[] add(double[] a, double[] b) {
        return new double[]{a[0] + b[0], a[1] + b[1], a[2] + b[2]};
    }

    private static double[] sub(double[] a, double[] b) {
        return new double[]{a[0] - b[0], a[1] - b[1], a[2] - b[2]};
    }

    private static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    private static double[] cross(double[] a, double[] b) {
        return new double[]{a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
    }

    private static double[] unit(double[] a) {
        double l = Math.sqrt(dot(a, a));
        return new double[]{a[0] / l, a[1] / l, a[2] / l};
    }
}
