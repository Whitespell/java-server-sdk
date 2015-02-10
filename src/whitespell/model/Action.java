package whitespell.model;

import java.util.HashMap;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         1/20/15
 *         whitespell.model
 */
public abstract class Action {

    /**
     * The Action object is an object that gets executed by an event, endpoint, or incoming action packet from an application-authorized server or Whitespell server.
     * This model stores which dependencies an action depends on before being executed
     * Example: Dependencies is group_id. In the login script saves the variable group_id and this action fires off.
     * Actions should NEVER depend on any sort of user input. Input has to be validated by endpoints and not by actions.
     * Actions are stored in the {@link Application} model
     * Actions are stateless, and should be scalable. The {@link whitespell.logic.FrostwaveClient} notifies the action if the variables comes available
     * @param actionName            The name of the action, mainly used for logging purposes and reverse-lookup
     * @param actionType            The type of the action
     * @param variables                  The variables that lead to this action being executed
     * @returns Action
     * @more https://docs.google.com/a/whitespell.com/document/d/1mVESYT38PDHDgMbiaJTMhrUZ5xHlUIGvpBSf-hXSaio/
     */

    private final String actionName;

    public String getActionName(){
        return this.actionName;
    }

    private final ActionType actionType;
    private final HashMap<String, Object> variables = new HashMap<>();

    public Action(String actionName, ActionType actionType, String[] variables) {
        this.actionName = actionName;
        this.actionType = actionType;

        for(String variable : variables) {
            this.variables.put(variable, null);
        }

    }

    /**
     * The execute function is called when the action needs execution. The execute function is basically the body of the request.
     */

    protected abstract void execute();


    /**
     * This function is called every checkReExecuteTimer() milliseconds. If this returns true, the action will be regenerated
     * and the users that are subscribed to this action will be re-notified.
     */
    protected abstract boolean doForceExecute();

    /**
     * The time in milliseconds the action is re-executed
     * @return
     */
    protected abstract long forceExecuteCheckTime();

    /**
     * The tie in milliseconds that this action can be cached for
     * @return
     */
    protected abstract long cacheTime();

}
