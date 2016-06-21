package app.session;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Chris.Ge
 */
//Here, we ensure that ViewTracker implements Serializable so that the Java Object Serialization system that
// undersits the Session persistence is capable of writing and reading the object.
public class ViewTracker implements Serializable {

    private Map<String, View> views = Maps.newConcurrentMap();

    public void increment(String uri) {

        views.merge(uri, new View(uri, 1), (view, view2) -> new View(uri, view.getCount() + 1));

    }


    List<View> list() {
        return Lists.newArrayList(views.values());
    }


    static class View implements Serializable {

        private final String uri;
        private int count;


        public View(String uri, int count) {
            this.uri = uri;
            this.count = count;
        }

        public String getUri() {
            return uri;
        }

        public int getCount() {
            return count;
        }
    }

}
