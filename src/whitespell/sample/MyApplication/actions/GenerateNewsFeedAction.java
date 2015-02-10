package whitespell.sample.MyApplication.actions;

import whitespell.model.Action;
import whitespell.model.ActionType;

import java.util.HashMap;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         2/4/15
 *         whitespell.sample.MyApplication.actions
 */
public class GenerateNewsFeedAction extends Action {
    public GenerateNewsFeedAction() {
        super("Generate Newsfeed", ActionType.Java, new String[]{
                "$userid"
        });
    }

    @Override
    protected void execute() {

    }

    @Override
    protected boolean doForceExecute() {
        return false;
    }

    @Override
    protected long forceExecuteCheckTime() {
        return 0;
    }

    @Override
    protected long cacheTime() {
        return 0;
    }
}
