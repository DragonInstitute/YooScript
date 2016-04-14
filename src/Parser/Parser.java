package Parser;

import Inter.*;
import Lexer.*;
import Symbols.Array;
import Symbols.Env;
import Symbols.Type;

import java.io.IOException;

public class Parser {

    private Lexer lexer;
    private Token lookAhead;
    Env top = null;
    int used = 0;

    public Parser(Lexer lexer) throws IOException {
        this.lexer = lexer;
        move();
    }

    void move() throws IOException {
        lookAhead = lexer.scan();
    }

    void error(String s) {
        throw new Error("near line " + lexer.line + ": " + s);
    }

    void match(int t) throws IOException {
        if (lookAhead.tag == t) {
            move();
        } else {
            error("syntax error");
        }

    }

    public void program() throws IOException {
        Stmt s = block();
        int begin = s.newLabel();
        int after = s.newLabel();
        s.emitLabel(begin);
        s.gen(begin, after);
        s.emitLabel(after);
    }

    Stmt block() throws IOException {
        match('{');
        Env savedEnv = top;
        top = new Env(top);
        decls();
        Stmt s = stmts();
        match('}');
        top = savedEnv;
        return s;
    }

    void decls() throws IOException {
        while (lookAhead.tag == Tag.BASIC) {
            Type p = type();
            Token token = lookAhead;
            match(Tag.ID);
            match(';');
            Id id = new Id((Word) token, p, used);
            top.put(token, id);
            used += p.width;
        }
    }

    Type type() throws IOException {
        Type p = (Type) lookAhead;
        System.out.println();
        match(Tag.BASIC);
        if (lookAhead.tag != '[') {
            return p;
        }
        return dims(p);
    }

    Type dims(Type p) throws IOException {
        match('[');
        Token token = lookAhead;
        match(Tag.NUM);
        match(']');
        if (lookAhead.tag == '[') {
            p = dims(p);
        }
        return new Array(((Num) token).value, p);
    }

    Stmt stmts() throws IOException {
        if (lookAhead.tag == '}') {
            return Stmt.Null;
        }
        return new Seq(stmt(), stmts());
    }

    Stmt stmt() throws IOException {
        Expr x;
        Stmt s, s1, s2;
        Stmt savedStmt; // for break
        switch (lookAhead.tag) {
            case ';':
                move();
                return Stmt.Null;
            case Tag.IF:
                match(Tag.IF);
                match('(');
                x = bool();
                match(')');
                s1 = stmt();
                if (lookAhead.tag != Tag.ELSE) {
                    return new If(x, s1);
                }
                match(Tag.ELSE);
                s2 = stmt();
                return new Else(x, s1, s2);
            case Tag.WHILE:
                While whilenode = new While();
                savedStmt = Stmt.Enclosing;
                Stmt.Enclosing = whilenode;
                match(Tag.WHILE);
                match('(');
                x = bool();
                match(')');
                s1 = stmt();
                whilenode.init(x, s1);
                Stmt.Enclosing = savedStmt;
                return whilenode;
            case Tag.DO:
                Do donode = new Do();
                savedStmt = Stmt.Enclosing;
                Stmt.Enclosing = donode;
                match(Tag.DO);
                s1 = stmt();
                match(Tag.WHILE);
                match('(');
                x = bool();
                match(')');
                match(';');
                donode.init(s1, x);
                Stmt.Enclosing = savedStmt;
                return donode;
            case Tag.BREAK:
                match(Tag.BREAK);
                match(';');
                return new Break();
            case '{':
                return block();
            default:
                return assign();
        }

    }

    Stmt assign() throws IOException {
        Stmt stmt;
        Token token = lookAhead;
        match(Tag.ID);
        Id id = top.get(token);
        if (id == null) {
            error(token.toString() + " undefined");
        }
        if (lookAhead.tag == '=') {
            move();
            stmt = new Set(id, bool());
        } else {
            Access x = offset(id);
            match('=');
            stmt = new SetElem(x, bool());
        }
        match(';');
        return stmt;
    }

    Expr bool() throws IOException {
        Expr x = join();
        while (lookAhead.tag == Tag.OR) {
            Token token = lookAhead;
            move();
            x = new Or(token, x, join());
        }
        return x;
    }

    Expr join() throws IOException {
        Expr x = equality();
        while (lookAhead.tag == Tag.AND) {
            Token token = lookAhead;
            move();
            x = new And(token, x, equality());
        }
        return x;
    }

    Expr equality() throws IOException {
        Expr x = rel();
        while (lookAhead.tag == Tag.EQ || lookAhead.tag == Tag.NE) {
            Token token = lookAhead;
            move();
            x = new Rel(token, x, rel());
        }
        return x;
    }

    Expr rel() throws IOException {
        Expr x = expr();
        switch (lookAhead.tag) {
            case '<':
            case Tag.LE:
            case Tag.GE:
            case '>':
                Token token = lookAhead;
                move();
                return new Rel(token, x, expr());
            default:
                return x;
        }
    }

    Expr expr() throws IOException {
        Expr x = term();
        while (lookAhead.tag == '+' || lookAhead.tag == '-') {
            Token token = lookAhead;
            move();
            x = new Arith(token, x, term());
        }
        return x;
    }

    Expr term() throws IOException {
        Expr x = unary();
        while (lookAhead.tag == '*' || lookAhead.tag == '/') {
            Token token = lookAhead;
            move();
            x = new Arith(token, x, unary());
        }
        return x;
    }

    Expr unary() throws IOException {
        if (lookAhead.tag == '-') {
            move();
            return new Unary(Word.minus, unary());
        } else if (lookAhead.tag == '!') {
            Token token = lookAhead;
            move();
            return new Not(token, unary());
        }
        return factor();
    }

    Expr factor() throws IOException {
        Expr x = null;
        switch (lookAhead.tag) {
            case '(':
                move();
                x = bool();
                match(')');
                return x;
            case Tag.NUM:
                x = new Constant(lookAhead, Type.Int);
                move();
                return x;
            case Tag.REAL:
                x = new Constant(lookAhead, Type.Float);
                move();
                return x;
            case Tag.TRUE:
                x = Constant.True;
                move();
                return x;
            case Tag.FALSE:
                x = Constant.False;
                move();
                return x;
            default:
                error("syntax error");
                return x;
            case Tag.ID:
                String s = lookAhead.toString();
                Id id = top.get(lookAhead);
                if (id == null) {
                    error(lookAhead.toString() + " undefined");
                }
                move();
                if (lookAhead.tag != '[') {
                    return id;
                } else {
                    return offset(id);
                }

        }
    }

    Access offset(Id a) throws IOException {
        Expr i, w, t1, t2, local;
        Type type = a.type;
        match('[');
        i = bool();
        match(']');
        type = ((Array) type).of;
        w = new Constant(type.width);
        t1 = new Arith(new Token('*'), i, w);
        local = t1;
        while (lookAhead.tag == '[') {
            match('[');
            i = bool();
            match(']');
            type = ((Array) type).of;
            w = new Constant(type.width);
            t1 = new Arith(new Token('*'), i, w);
            t2 = new Arith(new Token('+'), local, t1);
            local = t2;

        }
        return new Access(a, local, type);
    }


}
