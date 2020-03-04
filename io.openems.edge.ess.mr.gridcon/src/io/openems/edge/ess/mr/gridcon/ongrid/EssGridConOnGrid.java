package io.openems.edge.ess.mr.gridcon.ongrid;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.common.sum.GridMode;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.api.SymmetricEss;
import io.openems.edge.ess.mr.gridcon.EssGridcon;
import io.openems.edge.ess.mr.gridcon.StateController;
import io.openems.edge.ess.power.api.Power;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "MR.Gridcon.Ongrid", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE, //
		property = { EventConstants.EVENT_TOPIC + "=" + EdgeEventConstants.TOPIC_CYCLE_AFTER_PROCESS_IMAGE //
		}) //
public class EssGridConOnGrid extends EssGridcon
		implements ManagedSymmetricEss, SymmetricEss, ModbusSlave, OpenemsComponent, EventHandler {

	@Reference
	protected ComponentManager componentManager;

	@Reference
	private Power power;
	private Config config;

	public EssGridConOnGrid() {
		super(io.openems.edge.ess.mr.gridcon.ongrid.ChannelId.values());
	}

	@Activate
	void activate(ComponentContext context, Config c) throws OpenemsNamedException {
		this.config = c;
		EssGridConOnGrid.super.activate(context, c.id(), c.alias(), c.enabled(), c.gridcon_id(), c.bms_a_id(),
				c.bms_b_id(), c.bms_c_id());
		this.checkConfiguration(config);
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public void handleEvent(Event event) {
		super.handleEvent(event);
	}

	protected void calculateGridMode() throws IllegalArgumentException, OpenemsNamedException {
		GridMode gridMode = GridMode.ON_GRID;
		getGridMode().setNextValue(gridMode);
	}

	@Override
	public String debugLog() {
		return "StateObject: " + stateObject.getState().getName() + "| Next StateObject: "
				+ stateObject.getNextState().getName();
	}

	protected void checkConfiguration(Config config) throws OpenemsException {
		// TODO checks
	}

	@Override
	public Power getPower() {
		return power;
	}

	@Override
	protected ComponentManager getComponentManager() {
		return componentManager;
	}

	@Override
	protected io.openems.edge.ess.mr.gridcon.StateObject getFirstStateObjectUndefined() {
		return StateController.getStateObject(OnGridState.UNDEFINED);
	}

	@Override
	protected void initializeStateController(String gridconPCS, String b1, String b2, String b3) {
		StateController.initOnGrid(componentManager, gridconPCS, b1, b2, b3, config.enableIPU1(), config.enableIPU2(),
				config.enableIPU3(), config.parameterSet());
	}
}