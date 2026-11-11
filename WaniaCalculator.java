package wanialavender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

/**
 * ============================================================================
 *                            ✦ WANIA ✦
 *                  Simple Scientific Lavender Calculator
 * ============================================================================
 *
 * Theme Palette: Lavender Dream
 *  - Backdrop:       Soft Lavender Mist (#ECE6F6 -> #DCD2F0)
 *  - Chassis:        Frosted Lilac-Lavender (#E7DCF7 -> #D4C4EF)
 *  - Screen:         Midnight Lavender-Plum (#231834) with Glowing Lilac (#EBDCFF)
 *  - Number Keys:    Marshmallow Soft White (#FDFBFF) with Deep Violet text (#3E2263)
 *  - Function Keys:  Pastel Lavender (#E2D3F5) with Plum text (#4F2A79)
 *  - Equals Accent:  Vibrant Royal Violet (#8B5CF6) with Pure White (#FFFFFF)
 *  - Clear / Delete: Soft Rose-Lilac (#D058A8)
 *
 * Architecture:
 *  - Pure Java Swing & Java2D anti-aliased custom rendering
 *  - Zero external dependencies - runs on any Java JDK (8+)
 *  - Clean, simplified scientific layout designed for effortless daily calculations
 * ============================================================================
 */
public class WaniaCalculator extends JFrame {

    // --- Color Tokens (Lavender Palette) ---
    public static final Color BG_TOP               = new Color(0xF4, 0xEF, 0xFA);
    public static final Color BG_BOTTOM            = new Color(0xDC, 0xD0, 0xEE);

    public static final Color CHASSIS_TOP          = new Color(0xEB, 0xE0, 0xF8);
    public static final Color CHASSIS_BOTTOM       = new Color(0xD7, 0xC6, 0xEF);
    public static final Color CHASSIS_BORDER       = new Color(0xC5, 0xB0, 0xE5);
    public static final Color CHASSIS_SHADOW       = new Color(0x40, 0x2A, 0x60, 45);

    public static final Color SCREEN_BG            = new Color(0x23, 0x18, 0x34);
    public static final Color SCREEN_BORDER        = new Color(0x3E, 0x2C, 0x56);
    public static final Color SCREEN_DIGITS        = new Color(0xEB, 0xDC, 0xFF);
    public static final Color SCREEN_EXPR          = new Color(0xAE, 0x9B, 0xC8);
    public static final Color SCREEN_BADGE         = new Color(0xC0, 0x84, 0xFC);

    public static final Color NUM_BG               = new Color(0xFD, 0xFB, 0xFF);
    public static final Color NUM_HOVER            = new Color(0xFF, 0xFF, 0xFF);
    public static final Color NUM_PRESS            = new Color(0xEE, 0xE5, 0xF7);
    public static final Color NUM_TEXT             = new Color(0x3B, 0x22, 0x5D);

    public static final Color FN_BG                = new Color(0xE2, 0xD3, 0xF5);
    public static final Color FN_HOVER             = new Color(0xED, 0xE1, 0xFB);
    public static final Color FN_PRESS             = new Color(0xD2, 0xBF, 0xEB);
    public static final Color FN_TEXT              = new Color(0x4F, 0x29, 0x7A);

    public static final Color EQUALS_BG            = new Color(0x8B, 0x5C, 0xF6);
    public static final Color EQUALS_HOVER         = new Color(0x9A, 0x70, 0xF8);
    public static final Color EQUALS_PRESS         = new Color(0x76, 0x47, 0xE2);

    public static final Color CLEAR_BG             = new Color(0xD3, 0x5E, 0x9E);
    public static final Color CLEAR_HOVER          = new Color(0xDE, 0x71, 0xAD);
    public static final Color CLEAR_PRESS          = new Color(0xBD, 0x48, 0x89);

    // --- Calculator State ---
    private String expression = "";
    private String currentInput = "0";
    private boolean isDegreeMode = true;
    private boolean startNewNumber = true;
    private double lastAnswer = 0.0;

    // --- UI Labels ---
    private JLabel displayLabel;
    private JLabel exprLabel;
    private JLabel degBadge;

    public WaniaCalculator() {
        setTitle("Wania");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(410, 640);
        setLocationRelativeTo(null);

        initUI();
        initKeyBindings();
    }

    private void initUI() {
        // Outer Lavender Mist Canvas
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                GradientPaint bgPaint = new GradientPaint(0, 0, BG_TOP, 0, h, BG_BOTTOM);
                g2.setPaint(bgPaint);
                g2.fillRect(0, 0, w, h);

                // Gentle floral aura / soft glow
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillOval(w / 2 - 150, -50, 300, 200);
                g2.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(new EmptyBorder(16, 16, 18, 16));
        setContentPane(backgroundPanel);

        // Lavender Chassis
        LavenderChassis chassis = new LavenderChassis();
        chassis.setLayout(new BorderLayout(0, 12));
        chassis.setBorder(new EmptyBorder(18, 18, 18, 18));
        backgroundPanel.add(chassis, BorderLayout.CENTER);

        // Top Brand Header
        JPanel headerPanel = createHeader();
        chassis.add(headerPanel, BorderLayout.NORTH);

        // Center Container: Screen + Keypad
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);

        // Screen
        JPanel screen = createScreen();
        center.add(screen, BorderLayout.NORTH);

        // Keypad Grid
        JPanel keypad = createKeypad();
        center.add(keypad, BorderLayout.CENTER);

        chassis.add(center, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel brand = new JLabel("✦ WANIA ✦") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 180));
                g2.drawString(getText(), 1, 17);
                g2.setColor(new Color(0x4A, 0x2A, 0x73));
                g2.drawString(getText(), 0, 16);
                g2.dispose();
            }
        };
        brand.setFont(new Font("Serif", Font.BOLD, 17));
        brand.setForeground(new Color(0x4A, 0x2A, 0x73));

        JLabel subtitle = new JLabel("SCIENTIFIC");
        subtitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        subtitle.setForeground(new Color(0x8C, 0x6E, 0xB5));

        header.add(brand, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);
        return header;
    }

    private JPanel createScreen() {
        JPanel screen = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                // Outer border & inner glass
                g2.setColor(SCREEN_BORDER);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 18, 18));

                g2.setColor(SCREEN_BG);
                g2.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 16, 16));

                // Soft top lavender reflection
                GradientPaint sheen = new GradientPaint(
                        0, 0, new Color(216, 180, 254, 25),
                        0, h / 2f, new Color(0, 0, 0, 0)
                );
                g2.setPaint(sheen);
                g2.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 16, 16));
                g2.dispose();
            }
        };
        screen.setOpaque(false);
        screen.setPreferredSize(new Dimension(0, 105));
        screen.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Badges bar
        JPanel badgeBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeBar.setOpaque(false);
        degBadge = new JLabel("DEG");
        degBadge.setFont(new Font("SansSerif", Font.BOLD, 10));
        degBadge.setForeground(SCREEN_BADGE);
        degBadge.setBorder(new EmptyBorder(1, 4, 1, 4));
        badgeBar.add(degBadge);

        // Readout Area
        JPanel readout = new JPanel();
        readout.setLayout(new BoxLayout(readout, BoxLayout.Y_AXIS));
        readout.setOpaque(false);

        exprLabel = new JLabel(" ");
        exprLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
        exprLabel.setForeground(SCREEN_EXPR);
        exprLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        displayLabel = new JLabel("0");
        displayLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        displayLabel.setForeground(SCREEN_DIGITS);
        displayLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        readout.add(exprLabel);
        readout.add(Box.createVerticalStrut(2));
        readout.add(displayLabel);

        screen.add(badgeBar, BorderLayout.NORTH);
        screen.add(readout, BorderLayout.SOUTH);

        return screen;
    }

    /**
     * Clean, simple 5-column scientific keypad
     */
    private JPanel createKeypad() {
        JPanel panel = new JPanel(new GridLayout(6, 5, 6, 6));
        panel.setOpaque(false);

        // 6 Rows x 5 Columns = 30 simple, essential keys
        String[][] keys = {
                {"DEG", "FN"},   {"sin", "FN"},  {"cos", "FN"},  {"tan", "FN"},  {"AC", "CLEAR"},
                {"√", "FN"},     {"x²", "FN"},   {"^", "FN"},    {"π", "FN"},    {"⌫", "CLEAR"},
                {"log", "FN"},   {"7", "NUM"},   {"8", "NUM"},   {"9", "NUM"},   {"÷", "FN"},
                {"ln", "FN"},    {"4", "NUM"},   {"5", "NUM"},   {"6", "NUM"},   {"×", "FN"},
                {"(", "FN"},     {"1", "NUM"},   {"2", "NUM"},   {"3", "NUM"},   {"−", "FN"},
                {")", "FN"},     {"0", "NUM"},   {".", "NUM"},   {"=", "EQUALS"},{"＋", "FN"}
        };

        for (String[] key : keys) {
            String label = key[0];
            String type = key[1];

            LavenderButton.Type btnType = LavenderButton.Type.FUNCTION;
            if (type.equals("NUM")) btnType = LavenderButton.Type.NUMBER;
            else if (type.equals("EQUALS")) btnType = LavenderButton.Type.EQUALS;
            else if (type.equals("CLEAR")) btnType = LavenderButton.Type.CLEAR;

            LavenderButton btn = new LavenderButton(label, btnType);
            btn.setFont(getButtonFont(label, type));
            btn.addActionListener(e -> handleButton(label));
            panel.add(btn);
        }

        return panel;
    }

    private Font getButtonFont(String label, String type) {
        if (type.equals("NUM")) return new Font("SansSerif", Font.BOLD, 18);
        if (label.length() >= 3) return new Font("SansSerif", Font.BOLD, 12);
        return new Font("SansSerif", Font.BOLD, 15);
    }

    private void handleButton(String label) {
        try {
            switch (label) {
                // Digits
                case "0": case "1": case "2": case "3": case "4":
                case "5": case "6": case "7": case "8": case "9":
                    appendDigit(label);
                    break;
                case ".":
                    appendDot();
                    break;

                // Operators
                case "＋": handleOperator("+"); break;
                case "−": handleOperator("-"); break;
                case "×": handleOperator("*"); break;
                case "÷": handleOperator("/"); break;
                case "^": handleOperator("^"); break;

                // Brackets
                case "(":
                    if (startNewNumber || currentInput.equals("0")) {
                        expression += "(";
                    } else {
                        expression += currentInput + " * (";
                    }
                    currentInput = "0";
                    startNewNumber = true;
                    updateDisplay();
                    break;
                case ")":
                    expression += currentInput + ")";
                    startNewNumber = true;
                    updateDisplay();
                    break;

                // Clear & Delete
                case "AC":
                    expression = "";
                    currentInput = "0";
                    startNewNumber = true;
                    exprLabel.setText(" ");
                    updateDisplay();
                    break;
                case "⌫":
                    if (!startNewNumber && currentInput.length() > 1) {
                        currentInput = currentInput.substring(0, currentInput.length() - 1);
                        if (currentInput.equals("-")) currentInput = "0";
                    } else {
                        currentInput = "0";
                        startNewNumber = true;
                    }
                    updateDisplay();
                    break;

                // Constants
                case "π":
                    currentInput = formatNumber(Math.PI);
                    startNewNumber = true;
                    updateDisplay();
                    break;

                // Angle Toggle
                case "DEG":
                case "RAD":
                    isDegreeMode = !isDegreeMode;
                    degBadge.setText(isDegreeMode ? "DEG" : "RAD");
                    break;

                // Immediate Scientific Functions
                case "sin": case "cos": case "tan":
                case "log": case "ln": case "√": case "x²":
                    applyScientific(label);
                    break;

                // Equals
                case "=":
                    evaluate();
                    break;
            }
        } catch (Exception ex) {
            displayLabel.setText("Error");
            exprLabel.setText("Calculation Error");
            startNewNumber = true;
        }
    }

    private void appendDigit(String digit) {
        if (startNewNumber || currentInput.equals("0")) {
            currentInput = digit;
            startNewNumber = false;
        } else {
            if (currentInput.replace(".", "").length() < 14) {
                currentInput += digit;
            }
        }
        updateDisplay();
    }

    private void appendDot() {
        if (startNewNumber) {
            currentInput = "0.";
            startNewNumber = false;
        } else if (!currentInput.contains(".")) {
            currentInput += ".";
        }
        updateDisplay();
    }

    private void handleOperator(String op) {
        if (!expression.isEmpty() && startNewNumber) {
            char last = expression.charAt(expression.length() - 1);
            if ("+-*/^".indexOf(last) >= 0) {
                expression = expression.substring(0, expression.length() - 1) + op;
                updateDisplay();
                return;
            }
        }
        expression += currentInput + " " + op + " ";
        startNewNumber = true;
        updateDisplay();
    }

    private void applyScientific(String func) {
        double val = Double.parseDouble(currentInput);
        double res = 0.0;
        String desc = "";

        switch (func) {
            case "sin":
                res = Math.sin(isDegreeMode ? Math.toRadians(val) : val);
                desc = "sin(" + currentInput + ")";
                break;
            case "cos":
                res = Math.cos(isDegreeMode ? Math.toRadians(val) : val);
                desc = "cos(" + currentInput + ")";
                break;
            case "tan":
                res = Math.tan(isDegreeMode ? Math.toRadians(val) : val);
                desc = "tan(" + currentInput + ")";
                break;
            case "log":
                if (val <= 0) throw new ArithmeticException();
                res = Math.log10(val);
                desc = "log(" + currentInput + ")";
                break;
            case "ln":
                if (val <= 0) throw new ArithmeticException();
                res = Math.log(val);
                desc = "ln(" + currentInput + ")";
                break;
            case "√":
                if (val < 0) throw new ArithmeticException();
                res = Math.sqrt(val);
                desc = "√(" + currentInput + ")";
                break;
            case "x²":
                res = val * val;
                desc = "(" + currentInput + ")²";
                break;
        }

        exprLabel.setText(desc);
        currentInput = formatNumber(res);
        lastAnswer = res;
        startNewNumber = true;
        updateDisplay();
    }

    private void evaluate() {
        if (expression.isEmpty()) return;
        String fullExpr = expression + currentInput;
        try {
            double res = SimpleMathEvaluator.eval(fullExpr);
            String formatted = formatNumber(res);
            lastAnswer = res;
            exprLabel.setText(fullExpr + " =");
            currentInput = formatted;
            expression = "";
            startNewNumber = true;
            updateDisplay();
        } catch (Exception e) {
            displayLabel.setText("Error");
            exprLabel.setText(fullExpr);
            startNewNumber = true;
        }
    }

    private void updateDisplay() {
        displayLabel.setText(currentInput);
        if (!expression.isEmpty()) {
            exprLabel.setText(expression);
        }
        int len = currentInput.length();
        if (len > 12) {
            displayLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        } else if (len > 9) {
            displayLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        } else {
            displayLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        }
    }

    private String formatNumber(double val) {
        if (Double.isNaN(val)) return "NaN";
        if (Double.isInfinite(val)) return val > 0 ? "Infinity" : "-Infinity";
        if (val == (long) val) {
            return String.format("%d", (long) val);
        }
        DecimalFormat df = new DecimalFormat("0.########");
        return df.format(val);
    }

    private void initKeyBindings() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c >= '0' && c <= '9') appendDigit(String.valueOf(c));
                else if (c == '.') appendDot();
                else if (c == '+') handleOperator("+");
                else if (c == '-') handleOperator("-");
                else if (c == '*' || c == 'x' || c == 'X') handleOperator("*");
                else if (c == '/') handleOperator("/");
                else if (c == '^') handleOperator("^");
                else if (c == '=' || c == '\n') evaluate();
                else if (c == '(') handleButton("(");
                else if (c == ')') handleButton(")");
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) handleButton("⌫");
                else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) handleButton("AC");
            }
        });
    }

    // =========================================================================
    // CUSTOM SWING COMPONENTS (Lavender UI)
    // =========================================================================

    static class LavenderChassis extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int arc = 24;

            // Chassis Drop Shadow
            g2.setColor(CHASSIS_SHADOW);
            g2.fill(new RoundRectangle2D.Float(4, 6, w - 8, h - 8, arc + 4, arc + 4));

            // Lavender Rim
            g2.setColor(CHASSIS_BORDER);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

            // Chassis Gradient Fill
            GradientPaint gp = new GradientPaint(0, 0, CHASSIS_TOP, 0, h, CHASSIS_BOTTOM);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, arc - 2, arc - 2));

            // Subtle top highlight
            g2.setColor(new Color(255, 255, 255, 120));
            g2.drawLine(arc / 2, 3, w - arc / 2, 3);

            g2.dispose();
        }
    }

    static class LavenderButton extends JButton {
        public enum Type { NUMBER, FUNCTION, EQUALS, CLEAR }

        private final Type type;
        private boolean isHovered = false;
        private boolean isPressed = false;

        public LavenderButton(String text, Type type) {
            super(text);
            this.type = type;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                @Override
                public void mousePressed(MouseEvent e) { isPressed = true; repaint(); }
                @Override
                public void mouseReleased(MouseEvent e) { isPressed = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 12;

            Color baseBg, hoverBg, pressBg, textColor, shadowColor;

            switch (type) {
                case NUMBER:
                    baseBg = NUM_BG; hoverBg = NUM_HOVER; pressBg = NUM_PRESS;
                    textColor = NUM_TEXT; shadowColor = new Color(0x9A, 0x82, 0xB8, 70);
                    break;
                case EQUALS:
                    baseBg = EQUALS_BG; hoverBg = EQUALS_HOVER; pressBg = EQUALS_PRESS;
                    textColor = Color.WHITE; shadowColor = new Color(0x5B, 0x2A, 0xA3, 90);
                    break;
                case CLEAR:
                    baseBg = CLEAR_BG; hoverBg = CLEAR_HOVER; pressBg = CLEAR_PRESS;
                    textColor = Color.WHITE; shadowColor = new Color(0x8C, 0x30, 0x65, 80);
                    break;
                case FUNCTION:
                default:
                    baseBg = FN_BG; hoverBg = FN_HOVER; pressBg = FN_PRESS;
                    textColor = FN_TEXT; shadowColor = new Color(0x9A, 0x82, 0xB8, 60);
                    break;
            }

            int yOffset = isPressed ? 2 : 0;

            // Soft Shadow
            if (!isPressed) {
                g2.setColor(shadowColor);
                g2.fill(new RoundRectangle2D.Float(1, 2, w - 2, h - 2, arc, arc));
            }

            // Button Body
            Color currentBg = isPressed ? pressBg : (isHovered ? hoverBg : baseBg);
            g2.setColor(currentBg);
            g2.fill(new RoundRectangle2D.Float(1, 1 + yOffset, w - 2, h - 3, arc, arc));

            // Soft top rim
            if (!isPressed) {
                g2.setColor(new Color(255, 255, 255, 140));
                g2.draw(new RoundRectangle2D.Float(2, 2, w - 4, (h - 4) / 2f, arc - 2, arc - 2));
            }

            // Text
            g2.setColor(textColor);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = ((h + fm.getAscent() - fm.getDescent()) / 2) + yOffset;
            g2.drawString(getText(), tx, ty);

            g2.dispose();
        }
    }

    // =========================================================================
    // RECURSIVE DESCENT MATH EVALUATOR
    // =========================================================================
    static class SimpleMathEvaluator {
        public static double eval(final String str) {
            return new Object() {
                int pos = -1, ch;
                void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) { nextChar(); return true; }
                    return false;
                }
                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }
                double parseExpression() {
                    double x = parseTerm();
                    for (;;) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }
                double parseTerm() {
                    double x = parseFactor();
                    for (;;) {
                        if (eat('*') || eat('×')) x *= parseFactor();
                        else if (eat('/') || eat('÷')) x /= parseFactor();
                        else return x;
                    }
                }
                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();
                    double x;
                    int startPos = this.pos;
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }
                    if (eat('^')) x = Math.pow(x, parseFactor());
                    return x;
                }
            }.parse();
        }
    }

    // =========================================================================
    // MAIN LAUNCHER
    // =========================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new WaniaCalculator().setVisible(true);
        });
    }
}
