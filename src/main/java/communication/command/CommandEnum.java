package communication.command;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by szezso on 2017.08.19..
 */
public enum CommandEnum {
    SPEED("speed"),
    DST("dst"),
    CONGESTION("congestion");

    private String command;

    CommandEnum(String command) {
        this.command = command;
    }

    public static boolean contains(String command) {
        return Arrays.asList(CommandEnum.values()).stream()
                .filter(value -> value.command.equalsIgnoreCase(command))
                .collect(Collectors.toList()).size() > 0;
    }

    public String getCommand() {
        return this.command;
    }

    public static CommandEnum getNameByValue(String command) {
        return Arrays.stream(CommandEnum.values())
                .filter(c -> c.command.equalsIgnoreCase(command)).findFirst()
                .get();
    }

}
