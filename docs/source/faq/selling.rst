How can you sell costumes/skins?
================================

PetBlock does not contain a system for selling items. However, a common method to achieve this
feature is by selling permissions to each skin/costume/engine etc.

For example:

You have got a shop plugin which allows to sell permissions. You offer the permission **petblocks.pet.customcostumes.2** for sale.
When a player buys this permission it can use the skin at the player-heads costume category at slot 2.

You can also add a custom lore for the player to let them see if they bought the item or not. This sample shows a custom lore
at the block costume category at slot 1.


**before:**
::
    ############################

    # Wardrobe-settings

    # Settings for the petblock-wardrobe-category.
    # You can add new costumes by yourself!
    # id: item id in the GUI
    # damage: item damage in the the GUI
    # skin: item skin in the GUI (Name of the player or the skin URL)
    # name: item name in the GUI
    # unbreakable: item unbreakable tag in the GUI
    # lore: item lore in the GUI (Multiple lines support)

    ############################

    wardrobe:
      ordinary:
       1:
        id: 1
        damage: 0
        skin: 'none'
        name: 'none'
        unbreakable: false
        lore:
         - 'none'

**after:**
::
    ############################

    # Wardrobe-settings

    # Settings for the petblock-wardrobe-category.
    # You can add new costumes by yourself!
    # id: item id in the GUI
    # damage: item damage in the the GUI
    # skin: item skin in the GUI (Name of the player or the skin URL)
    # name: item name in the GUI
    # unbreakable: item unbreakable tag in the GUI
    # lore: item lore in the GUI (Multiple lines support)

    ############################

    wardrobe:
      ordinary:
       1:
        id: 1
        damage: 0
        skin: 'none'
        name: 'none'
        unbreakable: false
        lore:
         - '&7Bought: <permission>'


The placeholder **<permission>** gets replaced by **Yes** or **No** per default. You can change this in the config.yml:
::
    messages:
        perms-ico-yes: '&aYes'
        perms-ico-no: '&cNo'



