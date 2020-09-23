package service;

import model.events.IEventGenerator;
import view.IQueueController;

public interface IService {

    void simulate(
            IEventGenerator particularArrivalGenerator,
            IEventGenerator taxiArrivalGenerator,
            IEventGenerator busArrivalGenerator,
            double lightChangeTime,
            double crossingTime,
            double minutesToSimulate,
            IQueueController controller
    );
}
