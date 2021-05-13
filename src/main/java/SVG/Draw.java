package SVG;

import entities.DailyStockRating;

import java.util.ArrayList;
import java.util.HashMap;

public interface Draw {
    String draw(ArrayList<DailyStockRating> stocks);

}
