package simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum ScenarioEnum {
    SCENARIO1("Scenario1"),
    SCENARIO2("Scenario2");

    private String scenario;

    ScenarioEnum(String scenario) {
        this.scenario = scenario;
    }

    public String getScenario() {
        return scenario;
    }

    public static List<String> getAllScenario() {
        return Arrays.stream(ScenarioEnum.values()).map(ScenarioEnum::getScenario).collect(Collectors.toList());
    }

    public static ScenarioEnum getNameByValue(String scenario) {
        return Arrays.stream(ScenarioEnum.values())
                .filter(c -> c.scenario.equalsIgnoreCase(scenario)).findFirst()
                .get();
    }

}
