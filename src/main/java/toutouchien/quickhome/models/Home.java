package toutouchien.quickhome.models;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Home {
    private String name;
    private Location location;

    public Home(@NotNull String name, @NotNull Location location) {
        this.name = name;
        this.location = location;
    }

    public void name(String name) {
        this.name = name;
    }

    public void location(Location location) {
        this.location = location;
    }

    @NotNull
    public String name() {
        return name;
    }

    @NotNull
    public Location location() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        Home home = (Home) o;
        return Objects.equals(name, home.name)
                && Objects.equals(location, home.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location);
    }
}
