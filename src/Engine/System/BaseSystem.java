package Engine.System;

/*
 * @author Matthieu Le Boucher <matt.leboucher@gmail.com>
 */

import Engine.Main.Entity;
import Engine.System.Component.Component;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseSystem implements GameSystem {
    public void iterate(List<Entity> entities) {
        entities.forEach(entity -> getLocalSystemComponentsFor(entity).forEach(this::applyComponent));
    }

    public List<Component> getLocalSystemComponentsFor(Entity entity) {
        List<Component> systemComponents = getLocalSystemComponents();

        return entity.getComponents()
                .stream().filter(systemComponents::contains)
                .collect(Collectors.toList());
    }

    public abstract void applyComponent(Component component);

    public abstract List<Component> getLocalSystemComponents();
}
