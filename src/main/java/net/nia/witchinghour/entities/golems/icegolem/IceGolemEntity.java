package net.nia.witchinghour.entities.golems.icegolem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nia.witchinghour.entities.WitchingHourCommand;
import net.nia.witchinghour.entities.commands.FollowCommandGoal;
import net.nia.witchinghour.entities.commands.StayCommandGoal;

import java.util.UUID;

public class IceGolemEntity extends IronGolemEntity
        implements IceGolemCommands {

    private WitchingHourCommand command = WitchingHourCommand.FOLLOW;

    private UUID ownerUuid;

    private BlockPos stayPosition;

    private UUID commandTargetUuid;

    public IceGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean attacked = super.tryAttack(target);

        if (attacked && target instanceof LivingEntity livingTarget) {
            livingTarget.setFrozenTicks(200);
        }

        return attacked;
    }

    @Override
    public void setCommand(WitchingHourCommand command) {
        this.command = command;
    }

    @Override
    public WitchingHourCommand getCommand() {
        return command;
    }

    @Override
    protected void initGoals() {

        this.goalSelector.add(1, new StayCommandGoal(this));
        this.goalSelector.add(2, new FollowCommandGoal(this, 1.0f));
    }

    // =========================
    // OWNER
    // =========================

    public void setOwner(PlayerEntity player) {
        this.ownerUuid = player.getUuid();
    }

    public PlayerEntity getOwner() {
        if (ownerUuid == null) {
            return null;
        }

        return this.getWorld().getPlayerByUuid(ownerUuid);
    }

    @Override
    public MobEntity getMinion() {
        return this;
    }

    // =========================
    // STAY POSITION
    // =========================

    public void setStayPosition(BlockPos pos) {
        this.stayPosition = pos.toImmutable();
    }

    public BlockPos getStayPosition() {
        return stayPosition;
    }

    // =========================
    // COMMAND TARGET
    // =========================

    public LivingEntity getCommandTarget() {

        if (commandTargetUuid == null) {
            return null;
        }

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Entity entity = serverWorld.getEntity(commandTargetUuid);

            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        return null;
    }

    public void setCommandTarget(LivingEntity target) {

        if (target == null) {
            this.commandTargetUuid = null;
            return;
        }

        this.commandTargetUuid = target.getUuid();
    }

    // =========================
    // MINION
    // =========================

    @Override
    public boolean isMinion() {
        return true;
    }

    // =========================
    // NBT SAVE
    // =========================

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        if (ownerUuid != null) {
            nbt.putUuid("Owner", ownerUuid);
        }

        nbt.putString("Command", command.name());

        if (stayPosition != null) {
            nbt.putInt("StayX", stayPosition.getX());
            nbt.putInt("StayY", stayPosition.getY());
            nbt.putInt("StayZ", stayPosition.getZ());
        }

        if (commandTargetUuid != null) {
            nbt.putUuid("CommandTarget", commandTargetUuid);
        }
    }

    // =========================
    // NBT LOAD
    // =========================

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.containsUuid("Owner")) {
            ownerUuid = nbt.getUuid("Owner");
        }

        if (nbt.contains("Command", NbtElement.STRING_TYPE)) {
            try {
                command = WitchingHourCommand.valueOf(
                        nbt.getString("Command")
                );
            } catch (IllegalArgumentException ignored) {
                command = WitchingHourCommand.FOLLOW;
            }
        }

        if (nbt.contains("StayX", NbtElement.INT_TYPE)
                && nbt.contains("StayY", NbtElement.INT_TYPE)
                && nbt.contains("StayZ", NbtElement.INT_TYPE)) {

            stayPosition = new BlockPos(
                    nbt.getInt("StayX"),
                    nbt.getInt("StayY"),
                    nbt.getInt("StayZ")
            );
        }

        if (nbt.containsUuid("CommandTarget")) {
            commandTargetUuid = nbt.getUuid("CommandTarget");
        }
    }
}