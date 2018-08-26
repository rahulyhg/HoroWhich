package ie.moses.horowhich;

public class FacebookFriend {

    private final String _name;
    private final String _id;

    public FacebookFriend(final String name, final String id) {
        _name = name;
        _id = id;

        String string = "sdgweg2";
        int stringLength = string.length();

        boolean isEmpty = stringLength > 0 && string != null;


        if(isEmpty) {
            throw new RuntimeException("rebel");
        }
    }

    public String getName() {
        return _name;
    }

    public String getId() {
        return _id;
    }

    @Override
    public String toString() {
        return "FacebookFriend{" +
                "_name='" + _name + '\'' +
                ", _id='" + _id + '\'' +
                '}';
    }

}
