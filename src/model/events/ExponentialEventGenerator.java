package model.events;
import java.util.Random;

public class ExponentialEventGenerator implements IEventGenerator {
    private final Random randomGenerator = new Random();
    private final double media;
    private final EventType eventToGenerate;

    public ExponentialEventGenerator(double media, EventType eventName) {
        this.media = media;
        this.eventToGenerate = eventName;
    }

    @Override
    public Event generateEvent() {
        double random = randomGenerator.nextDouble();
        double timeForEvent = (-media)*Math.log(1-random);

        return new Event(random, timeForEvent, eventToGenerate);
    }
}
