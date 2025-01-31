/**
 * Copyright (c) 2020 Bosch.IO GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.filtermanagement;

import org.eclipse.hawkbit.repository.TargetFilterQueryManagement;
import org.eclipse.hawkbit.repository.builder.TargetFilterQueryUpdate;
import org.eclipse.hawkbit.repository.model.TargetFilterQuery;
import org.eclipse.hawkbit.ui.common.AbstractUpdateEntityWindowController;
import org.eclipse.hawkbit.ui.common.CommonUiDependencies;
import org.eclipse.hawkbit.ui.common.EntityWindowLayout;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyIdentifiableEntity;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyTargetFilterQuery;

/**
 * Controller for update target filter
 */
public class UpdateTargetFilterController extends
        AbstractUpdateEntityWindowController<ProxyTargetFilterQuery, ProxyTargetFilterQuery, TargetFilterQuery> {

    private final TargetFilterQueryManagement targetFilterManagement;
    private final TargetFilterAddUpdateLayout layout;
    private final Runnable closeFormCallback;
    private final ProxyTargetFilterValidator validator;

    private String nameBeforeEdit;

    /**
     * Constructor for UpdateTargetFilterController
     *
     * @param uiDependencies
     *            {@link CommonUiDependencies}
     * @param targetFilterManagement
     *            TargetFilterQueryManagement
     * @param layout
     *            TargetFilterAddUpdateLayout
     * @param closeFormCallback
     *            Runnable
     */
    public UpdateTargetFilterController(final CommonUiDependencies uiDependencies,
            final TargetFilterQueryManagement targetFilterManagement, final TargetFilterAddUpdateLayout layout,
            final Runnable closeFormCallback) {
        super(uiDependencies);

        this.targetFilterManagement = targetFilterManagement;
        this.layout = layout;
        this.closeFormCallback = closeFormCallback;
        this.validator = new ProxyTargetFilterValidator(uiDependencies);
    }

    @Override
    public EntityWindowLayout<ProxyTargetFilterQuery> getLayout() {
        return layout;
    }

    @Override
    protected ProxyTargetFilterQuery buildEntityFromProxy(final ProxyTargetFilterQuery proxyEntity) {
        final ProxyTargetFilterQuery target = new ProxyTargetFilterQuery();

        target.setId(proxyEntity.getId());
        target.setName(proxyEntity.getName());
        target.setQuery(proxyEntity.getQuery());

        nameBeforeEdit = proxyEntity.getName();

        return target;
    }

    @Override
    protected TargetFilterQuery persistEntityInRepository(final ProxyTargetFilterQuery entity) {
        final TargetFilterQueryUpdate targetFilterUpdate = getEntityFactory().targetFilterQuery().update(entity.getId())
                .name(entity.getName()).query(entity.getQuery());
        return targetFilterManagement.update(targetFilterUpdate);
    }

    @Override
    protected void postPersist() {
        closeFormCallback.run();
    }

    @Override
    protected String getDisplayableName(final TargetFilterQuery entity) {
        return entity.getName();
    }

    @Override
    protected String getDisplayableNameForFailedMessage(final ProxyTargetFilterQuery entity) {
        return entity.getName();
    }

    @Override
    protected Long getId(final TargetFilterQuery entity) {
        return entity.getId();
    }

    @Override
    protected Class<? extends ProxyIdentifiableEntity> getEntityClass() {
        return ProxyTargetFilterQuery.class;
    }

    @Override
    protected boolean isEntityValid(final ProxyTargetFilterQuery entity) {
        final String name = entity.getName();
        return validator.isEntityValid(entity,
                () -> hasNamedChanged(name) && targetFilterManagement.getByName(name).isPresent());
    }

    private boolean hasNamedChanged(final String trimmedName) {
        return !nameBeforeEdit.equals(trimmedName);
    }
}
