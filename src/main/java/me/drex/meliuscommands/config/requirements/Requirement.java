package me.drex.meliuscommands.config.requirements;

import com.google.gson.annotations.SerializedName;
import eu.pb4.predicate.api.BuiltinPredicates;
import eu.pb4.predicate.api.MinecraftPredicate;

public class Requirement {

    @SerializedName("command_path")
    public String commandPath = null;

    public boolean replace = false;

    public MinecraftPredicate require = BuiltinPredicates.operatorLevel(0);

}
