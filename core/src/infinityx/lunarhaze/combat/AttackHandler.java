package infinityx.lunarhaze.combat;

import infinityx.lunarhaze.models.AttackingGameObject;

/**
 * Base model class for all attack/combat systems.
 */
public class AttackHandler {

    /**
     * Counter for attacking (used to determine when to set attacking to false)
     */
    protected float attackCounter;

    /**
     * Counter for attack cooldowns
     */
    protected float attackCooldownCounter;


    /**
     * Attacking entity this class is controlling
     */
    protected AttackingGameObject entity;


    /**
     * @param entity attacking entity this class is controlling
     */
    public AttackHandler(AttackingGameObject entity) {
        attackCounter = 0f;
        attackCooldownCounter = entity.attackCooldown;
        this.entity = entity;
    }


    /**
     * @return whether a new attack can be started
     */
    public boolean canStartNewAttack() {
        return attackCooldownCounter >= entity.attackCooldown && !entity.isLockedOut();
    }


    /**
     * Handle all attacking related logic
     *
     * @param delta in seconds
     */
    public void update(float delta) {
        if (entity.isAttacking()) {
            processAttack(delta);
        } else {
            attackCooldownCounter += delta;
        }
    }

    /**
     * Initiates an attack
     */
    public void initiateAttack() {
        entity.setAttacking(true);
        entity.setImmune();
        attackCooldownCounter = 0f;
    }

    /**
     * Processes an attack, called every frame while attacking.
     */
    public void processAttack(float delta) {
        attackCounter += delta;
        if (attackCounter >= entity.attackLength) {
            endAttack();
        }
    }

    /**
     * Called when an attack ends
     */
    protected void endAttack() {
        entity.setAttacking(false);
        attackCounter = 0f;
    }

}
