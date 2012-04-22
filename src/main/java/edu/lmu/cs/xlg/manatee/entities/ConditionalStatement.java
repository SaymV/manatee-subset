package edu.lmu.cs.xlg.manatee.entities;

import java.util.List;

import edu.lmu.cs.xlg.util.Log;

public class ConditionalStatement extends Statement {

    public static class Arm extends Entity {
        Expression condition;
        Block block;

        public Arm(Expression condition, Block block) {
            this.condition = condition;
            this.block = block;
        }

        @Override
        public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
            condition.analyze(log, table, owner, inLoop);
            condition.assertBoolean("condition", log);
            block.analyze(log, table, owner, inLoop);
        }

        public Block getBlock() {
            return this.block;
        }

        public Expression getCondition() {
            return condition;
        }

        public void setCondition(Expression condition) {
            this.condition = condition;
        }

        public void setBlock(Block block) {
            this.block = block;
        }
    }

    private List<Arm> arms;
    private Block elsePart;

    public ConditionalStatement(List<Arm> arms, Block elsePart) {
        this.arms = arms;
        this.elsePart = elsePart;
    }

    public List<Arm> getArms() {
        return arms;
    }

    public Block getElsePart() {
        return elsePart;
    }

    @Override
    public void analyze(Log log, SymbolTable table, Subroutine owner, boolean inLoop) {
        for (Arm arm: arms) {
            arm.analyze(log, table, owner, inLoop);
        }
        if (elsePart != null) {
            elsePart.analyze(log, table, owner, inLoop);
        }
    }
}
