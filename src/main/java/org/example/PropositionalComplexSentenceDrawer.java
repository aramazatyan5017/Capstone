package org.example;

import org.example.domain.sentence.propositional.Literal;
import org.example.domain.sentence.propositional.GenericComplexPropositionalSentence;
import org.example.domain.PropositionalSentenceType;
import org.example.domain.sentence.propositional.PropositionalSentence;

import javax.swing.*;
import java.awt.*;

/**
 * @author aram.azatyan | 2/26/2024 11:15 AM
 */
public class PropositionalComplexSentenceDrawer extends JFrame {
    private final GenericComplexPropositionalSentence sentence;

    public PropositionalComplexSentenceDrawer(GenericComplexPropositionalSentence sentence) {
        if (sentence == null) throw new IllegalArgumentException("null param");

        this.sentence = sentence;
        setTitle("Binary Tree Drawer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawNode(g, sentence, getWidth() / 2, 50, getWidth() / 4);
    }

    private void drawNode(Graphics g, PropositionalSentence sentence, int x, int y, int xOffset) {
        int verticalGap = 80;

        if (sentence.type() == PropositionalSentenceType.GENERIC_COMPLEX) {
            GenericComplexPropositionalSentence genericComplexPropositionalSentence = (GenericComplexPropositionalSentence) sentence;
            if (genericComplexPropositionalSentence.getLeftSentence() != null) {
                int newX = x - xOffset;
                int newY = y + verticalGap;
                g.setColor(Color.BLACK);
                g.drawLine(x, y, newX, newY);
                drawNode(g, genericComplexPropositionalSentence.getLeftSentence(), newX, newY, xOffset / 2);
            }

            if (sentence.type() == PropositionalSentenceType.GENERIC_COMPLEX && genericComplexPropositionalSentence.getRightSentence() != null) {
                int newX = x + xOffset;
                int newY = y + verticalGap;
                g.setColor(Color.BLACK);
                g.drawLine(x, y, newX, newY);
                drawNode(g, genericComplexPropositionalSentence.getRightSentence(), newX, newY, xOffset / 2);
            }
        }

        String value = getValueAndChangeColor(sentence, g);
        int nodeWidth = 40;
        int nodeHeight = 40;
        g.fillOval(x - nodeWidth / 2, y - nodeHeight / 2, nodeWidth, nodeHeight);
        g.setColor(Color.BLACK);
        g.drawOval(x - nodeWidth / 2, y - nodeHeight / 2, nodeWidth, nodeHeight);
        g.setColor(Color.BLACK);

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(value);
        int textHeight = fm.getHeight();
        g.drawString(value, x - textWidth / 2, y + textHeight / 4);
    }

    private String getValueAndChangeColor(PropositionalSentence sentence, Graphics g) {
        if (sentence.type() == PropositionalSentenceType.LITERAL) {
            Literal literal = (Literal) sentence;
            g.setColor(literal.isNegated() ? new Color(255, 153, 153) : Color.GREEN);
            return (literal).getName();
        } else {
            GenericComplexPropositionalSentence complex = (GenericComplexPropositionalSentence) sentence;
            g.setColor(complex.isNegated() ? new Color(255, 153, 153) : Color.GREEN);
            return complex.getConnective().toString();
        }
    }

    public static void main(String[] args) throws Exception {
        new PropositionalComplexSentenceDrawer(new GenericComplexPropositionalSentence("(a | b) & (c | !d | e)")).setVisible(true);
    }
}
