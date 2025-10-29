package top.mrxiaom.sweet.autores.conditions;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.api.IAction;
import top.mrxiaom.pluginbase.utils.depend.PAPI;
import top.mrxiaom.pluginbase.utils.Util;

import java.util.List;

public class NumberCondition implements ICondition {
    public enum Operator {
        EQUALS("equals", "=="),
        LESS_THAN("less than", "smaller than", "<"),
        LARGER_THAN("larger than", "bigger than", ">"),
        LESS_THAN_OR_EQUALS("less than or equals", "smaller than or equals", "<="),
        LARGER_THAN_OR_EQUALS("larger than or equals", "bigger than or equals", ">=")
        ;
        final String[] aliases;
        Operator(String... aliases) {
            this.aliases = aliases;
        }

        @Nullable
        public static Operator parse(String s) {
            for (Operator value : values()) {
                for (String alias : value.aliases) {
                    if (s.equals(alias)) return value;
                }
            }
            return null;
        }
    }
    public final boolean reversed;
    public final String input;
    public final Operator operator;
    public final String output;
    public final List<IAction> denyCommands;

    public NumberCondition(boolean reversed, String input, Operator operator, String output, List<IAction> denyCommands) {
        this.reversed = reversed;
        this.input = input;
        this.operator = operator;
        this.output = output;
        this.denyCommands = denyCommands;
    }

    @Nullable
    private static Double num(String s) {
        return Util.parseDouble(s).orElse(null);
    }

    @Override
    public boolean match(Player player) {
        boolean b = match0(player);
        if (reversed) {
            return !b;
        }else {
            return b;
        }
    }

    public boolean match0(Player player) {
        String strIn = PAPI.setPlaceholders(player, input);
        String strOut = PAPI.setPlaceholders(player, output);
        if (operator == Operator.EQUALS) {
            if (strIn.equals(strOut)) return true;
            Double valueIn = num(strIn), valueOut = num(strOut);
            if (valueIn == null || valueOut == null) return false;
            return valueIn.equals(valueOut);
        }
        if (operator == Operator.LARGER_THAN_OR_EQUALS
        || operator == Operator.LESS_THAN_OR_EQUALS) {
            if (strIn.equals(strOut)) return true;
        }
        Double valueIn = num(strIn), valueOut = num(strOut);
        if (valueIn == null || valueOut == null) return false;
        switch (operator) {
            case LESS_THAN:
                return valueIn < valueOut;
            case LARGER_THAN:
                return valueIn > valueOut;
            case LESS_THAN_OR_EQUALS:
                return valueIn <= valueOut;
            case LARGER_THAN_OR_EQUALS:
                return valueIn >= valueOut;
        }
        return false;
    }

    @Override
    public List<IAction> getDenyCommands() {
        return denyCommands;
    }
}
