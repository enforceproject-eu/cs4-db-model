package org.n52.project.enforce.cs4.api;

import org.n52.project.enforce.cs4.api.impl.manorba.CS4PlayasDataRepository;
import org.n52.project.enforce.cs4.api.impl.minka.Cs4MinkaDataRepository;
import org.n52.project.enforce.cs4.util.Utils;

public abstract class BaseController {

    protected Cs4MinkaDataRepository dataRepository;

    protected CS4PlayasDataRepository cs4PlayasDataRepository;

    protected Utils utils;

    public BaseController(Cs4MinkaDataRepository dataRepository, Utils utils,
            CS4PlayasDataRepository cs4PlayasDataRepository) {
        this.dataRepository = dataRepository;
        this.cs4PlayasDataRepository = cs4PlayasDataRepository;
        this.utils = utils;
    }

}
