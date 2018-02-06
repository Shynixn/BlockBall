Can the pet walk instead of hopping like a rabbit?
==================================================

Yes it is possible to change the behaviour entity of the engines from **RABBIT** to **ZOMBIE**.

Do not forget to allow zombie spawning in your world/region.

**before**
::
    ############################

    # Engines-settings

    # Settings for the petblock-engines-category.
    # You can find all sound names at the bottom of this file.
    # [Warning! Sound names have changed between 1.8 and 1.9, select the right one. All default sounds get automatically translated to 1.9]
    # You can add new engines by yourself!

    # gui id: item id in the GUI
    # gui damage: item damage in the the GUI
    # gui skin: item skin in the GUI (Name of the player or the skin URL)
    # gui name: item name in the GUI
    # gui unbreakable: item unbreakable tag in the GUI
    # gui lore: item lore in the GUI (Multiple lines support)

    # behaviour entity types: RABBIT (HOPPING), ZOMBIE (WALKING)

    ############################

    engines:
       1:
         gui:
            id: 397
            damage: 3
            skin: 'textures.minecraft.net/texture/621668ef7cb79dd9c22ce3d1f3f4cb6e2559893b6df4a469514e667c16aa4'
            name: '&d&lPig'
            unbreakable: true
            lore:
              - '&7Permission: <permission>'
         behaviour:
            entity: 'RABBIT'
            riding: 'RUNNING'
         sound:
            ambient:
              name: 'PIG_IDLE'
              volume: 1.0
              pitch: 1.0
            walking:
              name: 'PIG_WALK'
              volume: 1.0
              pitch: 1.0


**after**
::
    ############################

    # Engines-settings

    # Settings for the petblock-engines-category.
    # You can find all sound names at the bottom of this file.
    # [Warning! Sound names have changed between 1.8 and 1.9, select the right one. All default sounds get automatically translated to 1.9]
    # You can add new engines by yourself!

    # gui id: item id in the GUI
    # gui damage: item damage in the the GUI
    # gui skin: item skin in the GUI (Name of the player or the skin URL)
    # gui name: item name in the GUI
    # gui unbreakable: item unbreakable tag in the GUI
    # gui lore: item lore in the GUI (Multiple lines support)

    # behaviour entity types: RABBIT (HOPPING), ZOMBIE (WALKING)

    ############################

    engines:
       1:
         gui:
            id: 397
            damage: 3
            skin: 'textures.minecraft.net/texture/621668ef7cb79dd9c22ce3d1f3f4cb6e2559893b6df4a469514e667c16aa4'
            name: '&d&lPig'
            unbreakable: true
            lore:
              - '&7Permission: <permission>'
         behaviour:
            entity: 'ZOMBIE'
            riding: 'RUNNING'
         sound:
            ambient:
              name: 'PIG_IDLE'
              volume: 1.0
              pitch: 1.0
            walking:
              name: 'PIG_WALK'
              volume: 1.0
              pitch: 1.0