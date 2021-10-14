package lemon.jpizza.libraries.jdraw;

import lemon.jpizza.Constants;
import lemon.jpizza.contextuals.Context;
import lemon.jpizza.contextuals.SymbolTable;
import lemon.jpizza.errors.Error;
import lemon.jpizza.errors.RTError;
import lemon.jpizza.objects.executables.Library;
import lemon.jpizza.objects.Obj;
import lemon.jpizza.objects.primitives.*;
import lemon.jpizza.Pair;
import lemon.jpizza.results.RTResult;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.awt.event.KeyEvent.*;

@SuppressWarnings("unused")
public class JDraw extends Library {
    static JFrame frame;
    static PizzaCanvas canvas;

    static boolean fullscreen = false;

    static Timer refreshLoop;

    static boolean changed = false;

    static Fnt font;
    static Integer strokeSize = null;

    static int frames = 0;
    static double start = 1;

    static final boolean[] mouseButtons = { false, false, false };
    static Point mousePos = new Point(0, 0);

    static final HashMap<String, Integer> keys = new HashMap<>(){{
        put("a", VK_A);
        put("b", VK_B);
        put("c", VK_C);
        put("d", VK_D);
        put("e", VK_E);
        put("f", VK_F);
        put("g", VK_G);
        put("h", VK_H);
        put("i", VK_I);
        put("j", VK_J);
        put("k", VK_K);
        put("l", VK_L);
        put("m", VK_M);
        put("n", VK_N);
        put("o", VK_O);
        put("p", VK_P);
        put("q", VK_Q);
        put("r", VK_R);
        put("s", VK_S);
        put("t", VK_T);
        put("u", VK_U);
        put("v", VK_V);
        put("w", VK_W);
        put("x", VK_X);
        put("y", VK_Y);
        put("z", VK_Z);

        put("up", VK_UP);
        put("down", VK_DOWN);
        put("left", VK_LEFT);
        put("right", VK_RIGHT);

        put("`", VK_BACK_QUOTE);
        put("'", VK_QUOTE);
        put("\"", VK_QUOTEDBL);

        put("0", VK_0);
        put("1", VK_1);
        put("2", VK_2);
        put("3", VK_3);
        put("4", VK_4);
        put("5", VK_5);
        put("6", VK_6);
        put("7", VK_7);
        put("8", VK_8);
        put("9", VK_9);

        put("!", VK_EXCLAMATION_MARK);
        put("@", VK_AT);
        put("#", VK_NUMBER_SIGN);
        put("$", VK_DOLLAR);
        put("^", VK_CIRCUMFLEX);
        put("&", VK_AMPERSAND);
        put("*", VK_ASTERISK);
        put("(", VK_LEFT_PARENTHESIS);
        put(")", VK_RIGHT_PARENTHESIS);

        put("-", VK_MINUS);
        put("=", VK_EQUALS);
        put("_", VK_UNDERSCORE);
        put("+", VK_PLUS);

        put("tab", VK_TAB);
        put("capslock", VK_CAPS_LOCK);
        put("enter", VK_ENTER);
        put("backspace", VK_BACK_SPACE);
        put("shift", VK_SHIFT);
        put("control", VK_CONTROL);

        put("[", VK_OPEN_BRACKET);
        put("]", VK_CLOSE_BRACKET);

        put("\\", VK_BACK_SLASH);

        put(";", VK_SEMICOLON);
        put(":", VK_COLON);

        put(",", VK_COMMA);
        put(".", VK_PERIOD);
        put("/", VK_SLASH);

        put(" ", VK_SPACE);
    }};

    static final HashMap<Integer, String> keycode = new HashMap<>(){{
        for (String key : keys.keySet())
            put(keys.get(key), key);
    }};

    static final HashMap<Integer, Boolean> keypressed = new HashMap<>(){{
        for (Integer key : keys.values())
            put(key, false);
    }};

    static final HashMap<Integer, Boolean> keytyped = new HashMap<>(){{
        for (Integer key : keys.values())
            put(key, false);
    }};

    static boolean queue = false;
    static ArrayList<DrawSlice> slices = new ArrayList<>();
    static ConcurrentHashMap<Point, Rect> pixels = new ConcurrentHashMap<>();

    public JDraw(String name) {
        super(name, "awt");
    }

    public static void initialize() {
        initialize("awt", JDraw.class, new HashMap<>(){{
            put("drawOval", Arrays.asList("x", "y", "width", "height", "color"));
            put("drawRect", Arrays.asList("x", "y", "width", "height", "color"));
            put("drawCircle", Arrays.asList("radius", "x", "y", "color"));
            put("drawText", Arrays.asList("txt", "x", "y", "color"));
            put("drawSquare", Arrays.asList("radius", "x", "y", "color"));
            put("drawLine", Arrays.asList("start", "end", "color"));
            put("drawPoly", Arrays.asList("points", "color"));
            put("tracePoly", Arrays.asList("points", "color"));
            put("setPixel", Arrays.asList("x", "y", "color"));
            put("chooseFile", Arrays.asList("path", "extension", "mode"));
            put("drawImage", Arrays.asList("x", "y", "filename"));
            put("setFont", Arrays.asList("fontName", "fontType", "fontSize"));
            put("setSize", Arrays.asList("width", "height"));
            put("setStrokeSize", Collections.singletonList("width"));
            put("setTitle", Collections.singletonList("value"));
            put("lockSize", Collections.singletonList("value"));
            put("gpuCompute", Collections.singletonList("value"));
            put("setIcon", Collections.singletonList("filename"));
            put("setBackgroundColor", Collections.singletonList("color"));
            put("mouseDown", Collections.singletonList("button"));
            put("keyDown", Collections.singletonList("key"));
            put("keyTyped", Collections.singletonList("key"));
            put("screenshot", Collections.singletonList("filename"));
            put("playSound", Collections.singletonList("filename"));
            put("exit", new ArrayList<>());
            put("start", new ArrayList<>());
            put("keyString", new ArrayList<>());
            put("mousePos", new ArrayList<>());
            put("mouseIn", new ArrayList<>());
            put("refresh", new ArrayList<>());
            put("toggleQRender", new ArrayList<>());
            put("qUpdate", new ArrayList<>());
            put("fps", new ArrayList<>());
            put("refreshLoop", new ArrayList<>());
            put("refreshUnloop", new ArrayList<>());
            put("init", new ArrayList<>());
            put("clear", new ArrayList<>());
        }});
        SymbolTable symb = Constants.LIBRARIES.get("awt").symbolTable;
        symb.define("SAVE", new Num(32));
        symb.define("OPEN", new Num(64));
    }

    public static Pair<Integer[], Error> getColor(Object col) {
        RTResult res = new RTResult();

        Obj lis = res.register(checkType(col, "list", Constants.JPType.List));
        if (res.error != null) return new Pair<>(null, res.error);

        List<Obj> list = lis.list;
        String errmsg = "Expected list composed of 3 0-255 integers";
        if (list.size() != 3) return new Pair<>(null, RTError.Type(
                lis.get_start(), lis.get_end(),
                errmsg,
                lis.get_ctx()
        ));

        Integer[] color = new Integer[3];
        for (int i = 0; i < 3; i++) {
            Obj obj = list.get(i);
            res.register(checkInt(obj));
            if (res.error != null) return new Pair<>(null, res.error);
            int num = obj.number.intValue();
            if (0 > num || num > 255) return new Pair<>(null, RTError.Type(
                    obj.get_start(), obj.get_end(),
                    errmsg,
                    obj.get_ctx()
            ));
            color[i] = num;
        }

        return new Pair<>(color, null);
    }

    public RTResult isInit() {
        if (frame == null || canvas == null) return new RTResult().failure(RTError.Init(
                pos_start, pos_end,
                "AWT not initialized",
                context
        ));
        return new RTResult();
    }

    @SuppressWarnings("DuplicatedCode")
    public static Pair<Point, Error> getCoords(Context ctx) {
        RTResult res = new RTResult();
        Obj cx = res.register(checkInt(ctx.symbolTable.get("x")));
        Obj cy = res.register(checkInt(ctx.symbolTable.get("y")));

        if (res.error != null) return new Pair<>(null, res.error);

        int x = cx.number.intValue();
        int y = cy.number.intValue();
        return new Pair<>(new Point(x, y), null);
    }

    @SuppressWarnings("DuplicatedCode")
    public static Pair<Point, Error> getDimensions(Context ctx) {
        RTResult res = new RTResult();
        Obj width = res.register(checkInt(ctx.symbolTable.get("width")));
        Obj height = res.register(checkInt(ctx.symbolTable.get("height")));

        if (res.error != null) return new Pair<>(null, res.error);

        int w = width.number.intValue();
        int h = height.number.intValue();
        return new Pair<>(new Point(w, h), null);
    }

    public RTResult execute_setBackgroundColor(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj col = (Obj) execCtx.symbolTable.get("color");

        Pair<Integer[], Error> r = getColor(col);
        if (r.b != null) return res.failure(r.b);
        Color color = new Color(r.a[0], r.a[1], r.a[2]);

        canvas.setBackground(color);
        changed = true;
        return res.success(new Null());
    }

    public RTResult execute_init(Context execCtx) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        frame = new JFrame("JPizzAwt");
        frame.setFocusTraversalKeysEnabled(false); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        start = System.currentTimeMillis();

        canvas = new PizzaCanvas();
        canvas.setDoubleBuffered(true);
        canvas.setFocusable(true);
        canvas.setFocusTraversalKeysEnabled(false);
        canvas.requestFocusInWindow();

        try {
            URL url = new URL("https://raw.githubusercontent.com/Lemon-Chad/jpizza/main/pizzico512.png");
            Image image = ImageIO.read(url);
            frame.setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }


        MouseListener mListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mousePos = e.getPoint();
            }

            @Override
            @SuppressWarnings("DuplicatedCode")
            public void mousePressed(MouseEvent e) {
                mousePos = e.getPoint();
                int index = switch (e.getButton()) {
                    case MouseEvent.BUTTON1 -> 0;
                    case MouseEvent.BUTTON2 -> 1;
                    case MouseEvent.BUTTON3 -> 2;
                    default -> -1;
                };
                if (index != -1) mouseButtons[index] = true;
            }

            @Override
            @SuppressWarnings("DuplicatedCode")
            public void mouseReleased(MouseEvent e) {
                mousePos = e.getPoint();
                int index = switch (e.getButton()) {
                    case MouseEvent.BUTTON1 -> 0;
                    case MouseEvent.BUTTON2 -> 1;
                    case MouseEvent.BUTTON3 -> 2;
                    default -> -1;
                };
                if (index != -1) mouseButtons[index] = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mousePos = e.getPoint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mousePos = new Point(0, 0);
            }
        };
        canvas.addMouseListener(mListener);

        KeyListener kListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                keypressed.put(e.getKeyCode(), true);
                keytyped.put(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keypressed.put(e.getKeyCode(), false);
            }
        };
        canvas.addKeyListener(kListener);

        return new RTResult().success(new Null());
    }

    @SuppressWarnings("DuplicatedCode")
    public RTResult execute_drawCircle(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj rad = res.register(checkPosInt(execCtx.symbolTable.get("radius")));
        if (res.error != null) return res;

        int radius = rad.number.intValue();

        Pair<Point, Error> p = getCoords(execCtx);
        if (p.b != null) return res.failure(p.b);
        Point pos = p.a;

        Pair<Integer[], Error> r = getColor(execCtx.symbolTable.get("color"));
        if (r.b != null) return res.failure(r.b);
        Color color = new Color(r.a[0], r.a[1], r.a[2]);

        draw(new Ovl(pos.x - radius, pos.y - radius, radius * 2, radius * 2, color));
        return res.success(new Null());
    }

    @SuppressWarnings("DuplicatedCode")
    public RTResult execute_drawSquare(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj rad = res.register(checkPosInt(execCtx.symbolTable.get("radius")));
        if (res.error != null) return res;

        int radius = rad.number.intValue();

        Pair<Point, Error> p = getCoords(execCtx);
        if (p.b != null) return res.failure(p.b);
        Point pos = p.a;

        Pair<Integer[], Error> r = getColor(execCtx.symbolTable.get("color"));
        if (r.b != null) return res.failure(r.b);
        Color color = new Color(r.a[0], r.a[1], r.a[2]);

        draw(new Rect(pos.x - radius / 2, pos.y - radius / 2, radius, radius, color));
        return res.success(new Null());
    }

    @SuppressWarnings("DuplicatedCode")
    public RTResult execute_drawOval(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj width = res.register(checkPosInt(execCtx.symbolTable.get("width")));
        Obj height = res.register(checkPosInt(execCtx.symbolTable.get("height")));
        if (res.error != null) return res;

        Pair<Point, Error> p = getCoords(execCtx);
        if (p.b != null) return res.failure(p.b);
        Point pos = p.a;

        p = getDimensions(execCtx);
        if (p.b != null) return res.failure(p.b);
        Point dim = p.a;

        Pair<Integer[], Error> r = getColor(execCtx.symbolTable.get("color"));
        if (r.b != null) return res.failure(r.b);
        Color color = new Color(r.a[0], r.a[1], r.a[2]);

        draw(new Ovl(pos.x - dim.x / 2, pos.y - dim.y / 2, dim.x, dim.y, color));
        return res.success(new Null());
    }

    public RTResult poly(Context execCtx, boolean outln) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj lx = res.register(checkType(execCtx.symbolTable.get("points"), "list", Constants.JPType.List));
        if (res.error != null) return res;
        List<Obj> lst = lx.list;

        Point[] points = new Point[lst.size()];
        for (int i = 0; i < lst.size(); i++) {
            Obj p = lst.get(i);

            res.register(checkType(p, "list", Constants.JPType.List));
            if (res.error != null) return res;
            List<Obj> pL = p.list;

            if (pL.size() != 2) return res.failure(RTError.Type(
                    p.get_start(), p.get_end(),
                    "Expected coordinates (list of 2 numbers)",
                    context
            ));

            res.register(checkInt(pL.get(0)));
            res.register(checkInt(pL.get(1)));
            if (res.error != null) return res;

            int x = pL.get(0).number.intValue();
            int y = pL.get(1).number.intValue();

            points[i] = new Point(x, y);
        }

        Pair<Integer[], Error> r = getColor(execCtx.symbolTable.get("color"));
        if (r.b != null) return res.failure(r.b);
        Color color = new Color(r.a[0], r.a[1], r.a[2]);

        draw(new Polygon(points, color, outln, strokeSize));
        return res.success(new Null());
    }

    public RTResult execute_drawPoly(Context execCtx) {
        return poly(execCtx, false);
    }

    public RTResult execute_tracePoly(Context execCtx) {
        return poly(execCtx, true);
    }

    @SuppressWarnings("DuplicatedCode")
    public RTResult execute_drawRect(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj width = res.register(checkPosInt(execCtx.symbolTable.get("width")));
        Obj height = res.register(checkPosInt(execCtx.symbolTable.get("height")));
        if (res.error != null) return res;

        Pair<Point, Error> p = getCoords(execCtx);
        if (p.b != null) return res.failure(p.b);
        Point pos = p.a;

        p = getDimensions(execCtx);
        if (p.b != null) return res.failure(p.b);
        Point dim = p.a;

        Pair<Integer[], Error> r = getColor(execCtx.symbolTable.get("color"));
        if (r.b != null) return res.failure(r.b);
        Color color = new Color(r.a[0], r.a[1], r.a[2]);

        draw(new Rect(pos.x - dim.x / 2, pos.y - dim.y / 2, dim.x, dim.y, color));
        return res.success(new Null());
    }

    public RTResult execute_setPixel(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Pair<Integer[], Error> r = getColor(execCtx.symbolTable.get("color"));
        if (r.b != null) return res.failure(r.b);
        Color color = new Color(r.a[0], r.a[1], r.a[2]);

        Pair<Point, Error> p = getCoords(execCtx);
        if (p.b != null) return res.failure(p.b);
        Point pos = p.a;

        if (res.error != null) return res;
        setPixel(pos, color);
        return res.success(new Null());
    }

    public RTResult execute_setTitle(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj value = (Obj) execCtx.symbolTable.get("value");
        frame.setTitle(value.toString());

        return res.success(new Null());
    }

    public RTResult execute_lockSize(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj value = ((Obj) execCtx.symbolTable.get("value")).bool();
        if (value.jptype != Constants.JPType.Boolean) return res.failure(RTError.Type(
                value.get_start(), value.get_end(),
                "Expected bool",
                execCtx
        ));
        frame.setResizable(!value.boolval);

        return res.success(new Null());
    }

    public RTResult execute_gpuCompute(Context execCtx) {
        RTResult res = new RTResult();

        Obj value = ((Obj) execCtx.symbolTable.get("value")).bool();
        if (value.jptype != Constants.JPType.Boolean) return res.failure(RTError.Type(
                value.get_start(), value.get_end(),
                "Expected bool",
                execCtx
        ));
        System.setProperty("sun.java2d.opengl", value.boolval ? "true" : "false");

        return res.success(new Null());
    }

    public RTResult execute_setFont(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        String name = execCtx.symbolTable.get("fontName").toString();

        String fontT = execCtx.symbolTable.get("fontType").toString();
        int fontType = switch (fontT) {
            case "B" -> Font.BOLD;
            case "I" -> Font.ITALIC;
            default -> Font.PLAIN;
        };

        Obj s = res.register(checkPosInt(execCtx.symbolTable.get("fontSize")));
        if (res.error != null) return res;
        int fontSize = s.number.intValue();

        font = new Fnt(name, fontType, fontSize);

        return res.success(new Null());
    }

    @SuppressWarnings("DuplicatedCode")
    public RTResult execute_setSize(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj width = res.register(checkPosInt(execCtx.symbolTable.get("width")));
        Obj height = res.register(checkPosInt(execCtx.symbolTable.get("height")));

        if (res.error != null) return res;

        Dimension dim = new Dimension(width.number.intValue(), height.number.intValue());

        canvas.setPreferredSize(dim);
        changed = true;

        return res.success(new Null());
    }

    public RTResult execute_setStrokeSize(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj width = res.register(checkPosInt(execCtx.symbolTable.get("width")));

        if (res.error != null) return res;

        strokeSize = width.number.intValue();

        return res.success(new Null());
    }

    @SuppressWarnings("DuplicatedCode")
    public RTResult execute_drawText(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Pair<Point, Error> p = getCoords(execCtx);
        if (p.b != null) return res.failure(p.b);
        Point pos = p.a;

        Pair<Integer[], Error> col = getColor(execCtx.symbolTable.get("color"));
        if (col.b != null) return res.failure(col.b);
        Color color = new Color(col.a[0], col.a[1], col.a[2]);

        Obj txt = res.register(checkType(execCtx.symbolTable.get("txt"), "String", Constants.JPType.String));
        if (res.error != null) return res;
        String msg = txt.string;

        draw(new Txt(pos.x, pos.y, msg, color, font));
        return res.success(new Null());
    }

    public Pair<Point, Error> extractPoint(Obj var) {
        RTResult res = new RTResult();

        res.register(checkType(var, "list", Constants.JPType.List));
        if (res.error != null) return new Pair<>(null, res.error);

        if (var.list.size() != 2) return new Pair<>(null, RTError.Type(
                var.get_start(), var.get_end(),
                "Expected point ([x, y])",
                var.get_ctx()
        ));

        Obj x = var.list.get(0);
        Obj y = var.list.get(1);

        res.register(checkInt(x));
        if (res.error != null) return new Pair<>(null, res.error);
        res.register(checkInt(y));
        if (res.error != null) return new Pair<>(null, res.error);

        return new Pair<>(new Point(x.number.intValue(), y.number.intValue()), null);
    }

    @SuppressWarnings("DuplicatedCode")
    public RTResult execute_drawLine(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Pair<Point, Error> dble;
        dble = extractPoint((Obj) execCtx.symbolTable.get("start"));
        if (dble.b != null) return res.failure(dble.b);
        Point start = dble.a;

        dble = extractPoint((Obj) execCtx.symbolTable.get("end"));
        if (dble.b != null) return res.failure(dble.b);
        Point end = dble.a;

        Pair<Integer[], Error> dblc = getColor(execCtx.symbolTable.get("color"));
        if (dblc.b != null) return res.failure(dblc.b);
        Color color = new Color(dblc.a[0], dblc.a[1], dblc.a[2]);

        draw(new Line(start, end, color, strokeSize));

        return res.success(new Null());
    }

    public RTResult execute_exit(Context execCtx) {
        RTResult res = isInit();
        if (res.error != null) return res;

        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

        return res.success(new Null());
    }

    @SuppressWarnings("DuplicatedCode")
    public RTResult execute_drawImage(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Pair<Point, Error> p = getCoords(execCtx);
        if (p.b != null) return res.failure(p.b);
        Point pos = p.a;

        String filename = execCtx.symbolTable.get("filename").toString();

        try {
            draw(new Img(pos.x, pos.y, filename));
        } catch (IOException e) {
            return res.failure(RTError.Internal(
                    pos_start, pos_end,
                    "Encountered IOException " + e.toString(),
                    execCtx
            ));
        }
        return res.success(new Null());
    }

    public RTResult execute_setIcon(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        String filename = execCtx.symbolTable.get("filename").toString();

        try {
            Image img = ImageIO.read(new File(filename));
            frame.setIconImage(img);
        } catch (IOException e) {
            return res.failure(RTError.Internal(
                    pos_start, pos_end,
                    "Encountered IOException " + e.toString(),
                    execCtx
            ));
        }
        return res.success(new Null());
    }

    public RTResult execute_playSound(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        String filename = execCtx.symbolTable.get("filename").toString();

        try {
            File soundFile = new File(filename);

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat audioFormat = audioInputStream.getFormat();

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

            new PlayThread(audioFormat, sourceDataLine, audioInputStream).start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            return res.failure(RTError.Internal(
                    pos_start, pos_end,
                    "Encountered IOException " + e.toString(),
                    execCtx
            ));
        }
        return res.success(new Null());
    }

    public RTResult execute_start(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);

        return res.success(new Null());
    }

    public RTResult execute_refresh(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        refresh();

        return res.success(new Null());
    }

    public RTResult execute_qUpdate(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        if (changed)
            canvas.push(slices, pixels);

        return res.success(new Null());
    }

    public RTResult execute_refreshLoop(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        if (refreshLoop != null) refreshLoop.stop();

        ActionListener taskPerformer = e -> refresh();
        Timer timer = new Timer(10, taskPerformer);
        timer.start();

        refreshLoop = timer;

        return res.success(new Null());
    }

    public RTResult execute_refreshUnloop(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        if (refreshLoop != null) {
            refreshLoop.stop();
            refreshLoop = null;
        }

        return res.success(new Null());
    }

    public RTResult execute_clear(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        flush();

        return res.success(new Null());
    }

    public RTResult execute_chooseFile(Context execCtx) {
        RTResult res = new RTResult();

        JFileChooser chooser = new JFileChooser(execCtx.symbolTable.get("path").toString());

        Obj fnef = (Obj) execCtx.symbolTable.get("extension");
        if (!(fnef instanceof Null)) {
            res.register(checkType(fnef, "list", Constants.JPType.List));
            if (res.error != null) return res;
            List<Obj> dat = fnef.list;

            if (dat.size() != 2) return res.failure(RTError.Type(
                    fnef.get_start(), fnef.get_end(),
                    "Expected 2 elements",
                    context
            ));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(dat.get(0).toString(), dat.get(1).toString());
            chooser.addChoosableFileFilter(filter);
            chooser.setFileFilter(filter);
        }

        Obj md = res.register(checkPosInt(execCtx.symbolTable.get("mode")));
        if (res.error != null) return res;

        int mode = md.number.intValue();
        Integer result = switch (mode) {
            case 32 -> chooser.showSaveDialog(null);
            case 64 -> chooser.showOpenDialog(null);
            default -> null;
        };

        if (result == null) return res.failure(RTError.Type(
                md.get_start(), md.get_end(),
                "Expected mode",
                context
        ));
        else if (result == JFileChooser.APPROVE_OPTION)
            return res.success(new Str(chooser.getSelectedFile().getAbsolutePath()));
        else
            return res.success(new Null());
    }

    public RTResult execute_fps(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        double time = (System.currentTimeMillis() - start) / 1000;

        return res.success(new Num(frames / time));
    }

    public RTResult execute_toggleQRender(Context execCtx) {
        queue = !queue;

        return new RTResult().success(new Null());
    }

    @SuppressWarnings("DuplicatedCode")
    public RTResult execute_keyDown(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        String key = execCtx.symbolTable.get("key").toString().toLowerCase();

        if (!keys.containsKey(key)) return res.failure(RTError.InvalidArgument(
                pos_start, pos_end,
                "Invalid key",
                execCtx
        ));

        return res.success(new Bool(keypressed.get(keys.get(key))));
    }

    @SuppressWarnings("DuplicatedCode")
    public RTResult execute_keyTyped(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        String key = execCtx.symbolTable.get("key").toString().toLowerCase();

        if (!keys.containsKey(key)) return res.failure(RTError.InvalidArgument(
                pos_start, pos_end,
                "Invalid key",
                execCtx
        ));

        boolean typed = keytyped.get(keys.get(key));
        keytyped.replace(keys.get(key), false);
        return res.success(new Bool(typed));
    }

    public RTResult execute_keyString(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        StringBuilder keystring = new StringBuilder();
        for (Integer key : keytyped.keySet()) {
            if (keytyped.get(key) && keycode.containsKey(key) && keycode.get(key).length() == 1)
                keystring.append(keycode.get(key));
            keytyped.replace(key, false);
        }
        return res.success(new Str(keystring.toString()));
    }

    public RTResult execute_mouseDown(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj i = res.register(checkPosInt(execCtx.symbolTable.get("button")));
        if (res.error != null) return res;
        int index = i.number.intValue();

        if (index > 2) return res.failure(RTError.Range(
                i.get_start(), i.get_end(),
                "Expected number where 0 <= n <= 2",
                execCtx
        ));

        return res.success(new Bool(mouseButtons[index]));
    }

    public RTResult execute_mousePos(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Point canvPos = canvas.getMousePosition();
        mousePos = canvPos != null ? canvPos : new Point(-1, -1);
        return res.success(
                new PList(Arrays.asList(
                        new Num(mousePos.x),
                        new Num(mousePos.y)
                ))
        );
    }

    public RTResult execute_mouseIn(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        return res.success(new Bool(canvas.getMousePosition() != null));
    }

    public RTResult execute_screenshot(Context execCtx) {
        RTResult res = new RTResult();

        res.register(isInit());
        if (res.error != null) return res;

        Obj fn = (Obj) execCtx.symbolTable.get("filename");
        String filename = fn.toString();

        BufferedImage img = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        canvas.paint(img.createGraphics());
        File imageFile = new File("." + File.separator + filename);

        boolean fileCreated;
        try {
            fileCreated = imageFile.createNewFile();
            ImageIO.write(img, "jpeg", imageFile);
        } catch (IOException e) {
            return res.failure(RTError.Internal(
                    fn.get_start(), fn.get_end(),
                    "Encountered IOException " + e.toString(),
                    execCtx
            ));
        }

        return res.success(new Bool(fileCreated));
    }

    static void refresh() {
        if (changed) {
            canvas.repaint();
            changed = false;
        }
        frames++;
    }

    static void draw(DrawSlice o) {
        changed = true;
        if (queue)
            slices.add(o);
        else
            canvas.draw(o);
    }

    static void setPixel(Point p, Color color) {
        changed = true;
        if (queue) {
            Rect r = new Rect(p.x, p.y, 1, 1, color);

            if (pixels.containsKey(p))
                pixels.replace(p, r);
            else
                pixels.put(p, r);
        } else
            canvas.setPixel(p, color);
    }

    static void flush() {
        changed = true;
        if (queue) {
            slices = new ArrayList<>();
            pixels = new ConcurrentHashMap<>();
        } else canvas.flush();
    }
}
