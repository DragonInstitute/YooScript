package Frontend.Lexer;

import Frontend.Symbols.Type;

import java.io.IOException;
import java.util.Hashtable;

public class Lexer {

    public static int line = 1;
    char peek = ' ';
    Hashtable words = new Hashtable();

    void reserve(Word w) {
        words.put(w.lexeme, w);

    }

    public Lexer() {
        reserve(new Word("if", Tag.IF));
        reserve(new Word("else", Tag.ELSE));
        reserve(new Word("while", Tag.WHILE));
        reserve(new Word("do", Tag.DO));
        reserve(new Word("break", Tag.BREAK));
        reserve(Word.True);
        reserve(Word.False);
        reserve(Type.Int);
        reserve(Type.Char);
        reserve(Type.Float);
        reserve(Type.Bool);
    }

    void readch() throws IOException {
        peek = (char) System.in.read();
    }

    boolean readch(char c) throws IOException {
        readch();
        if (peek != c) {
            return false;
        }
        peek = ' ';
        return true;
    }

    public Token scan() throws IOException {
        for (; ; readch()) {
            if (peek == ' ' || peek == '\t') {
                continue;
            } else if (peek == '\n') {
                line++;
            } else {
                break;
            }
        }

        switch (peek) {
            case '&':
                if (readch('&')) {
                    return Word.and;
                } else {
                    return new Token('&');
                }
            case '|':
                if (readch('|')) {
                    return Word.or;
                } else {
                    return new Token('|');
                }
            case '=':
                if (readch('=')) {
                    return Word.eq;
                } else {
                    return new Token('=');
                }
            case '!':
                if (readch('=')) {
                    return Word.ne;
                } else {
                    return new Token('!');
                }
            case '<':
                if (readch('=')) {
                    return Word.le;
                } else {
                    return new Token('<');
                }
            case '>':
                if (readch('=')) {
                    return Word.ge;
                } else {
                    return new Token('>');
                }

        }

        if (Character.isDigit(peek)) {
            int val = 0;
            do {
                val = val * 10 + Character.digit(peek, 10);
                readch();
            } while (Character.isDigit(peek));

            if (peek != '.') {
                return new Num(val);
            }
            float fl = val;
            float d = 10;
            for (; ; ) {
                readch();
                if (!Character.isDigit(peek)) {
                    break;
                }
                fl = fl + Character.digit(peek, 10) / d;
                d *= 10;
            }
            return new Real(fl);

        }

        if (Character.isLetter(peek)) {
            StringBuffer buffer = new StringBuffer();
            do {
                buffer.append(peek);
                readch();
            } while (Character.isLetterOrDigit(peek));
            String s = buffer.toString();
            Word word = (Word) words.get(s);
            if (word != null) {
                return word;
            }
            word = new Word(s, Tag.ID);
            words.put(s, word);
            return word;

        }
        Token token = new Token(peek);
        peek = ' ';
        return token;
    }
}
