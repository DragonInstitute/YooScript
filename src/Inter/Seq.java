package Inter;

public class Seq extends Stmt {

    Stmt stmt1, stmt2;

    public Seq(Stmt s1, Stmt s2) {
        stmt1 = s1;
        stmt2 = s2;
    }

    public void gen(int b, int a) {
        if (stmt1 == Stmt.Null) {
            stmt1.gen(b, a);
        } else if (stmt2 == Stmt.Null) {
            stmt1.gen(b, a);
        } else {
            int label = newLabel();
            stmt1.gen(b, label);
            emitLabel(label);
            stmt2.gen(label, a);
        }
    }


}
