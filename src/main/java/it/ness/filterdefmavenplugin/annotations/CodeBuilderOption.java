package it.ness.filterdefmavenplugin.annotations;

import java.util.ArrayList;
import java.util.List;

public enum CodeBuilderOption {

    EXECUTE_ALWAYS, WITHOUT_PARAMETERS;

    public static CodeBuilderOption[] from(String value) {
        if (value.contains(",")) {
            String[] options = value.split(",");
            List<CodeBuilderOption> codeBuilderOptions = new ArrayList<>();
            for (String opt : options) {
                opt = opt.replace("{", "").replace("}", "").replace("CodeBuilderOption.", "");
                codeBuilderOptions.add(CodeBuilderOption.valueOf(value));
            }
            return codeBuilderOptions.toArray(new CodeBuilderOption[]{});
        } else {
            value = value.replace("{", "").replace("}", "").replace("CodeBuilderOption.", "");
            return new CodeBuilderOption[]{CodeBuilderOption.valueOf(value)};
        }
    }
}
