## 24.2.0 - 17-07-2025 - 535406ed

### Features

- <a009b2f3> MFXMenu: implement navigation with arrow keys
- <142e9946> Implement my own menus API
- <60784802> Implement my own Key input API
- <28259c10> Backport OSUtils from another project of mine
- <a84470fa> Add convenience configuration methods to MFXPopups
- <1dd748df> Remade AnchorHandlers for maximum flexibility

### Bug Fixes

- <8ab203d8> Apply reset stylesheet to all popovers
- <4604213b> MFXTooltip: do not call hide from the TooltipTracker property if the new value is the same as the old one
- <40404c53> MFXTooltip: reset the tracker in the peer's hide method to catch auto-hide calls
- <eb28a340> AnchorHandlers: fix ADJACENT_INWARDS positioning mode for BOTTOM anchors
- <a51d82f0> MFXPopover: resetting styles on root inline prevents the values from being overridden. Apply a CSSFragment (stylesheet) instead

### Refactoring

- <201e8001> Make MFXMenuContent a Control to allow custom implementations (will implement virtualized menu content in components module)
- <65d4c1da> MFXMenuCell: override pref sizes rather than min
- <d7036b72> Add MFXMenu to MFXPopups builder
- <e9bd5b15> MFXMenu: improve key, navigation and focus handling
- <541a29e6> MFXPopup: expose the root as a generic Parent node
- <a28c4d18> MFXPopups: make config builders build the default config
- <75205521> MFXTooltip: throw error if tooltip is already installed
- <2a15a253> MFXPopups: fix configure(...) methods
- <a1301782> MFXTooltip: reorder some properties and remove local config as not necessary
- <42a02009> WhenEvent: add asFilter(boolean) convenience method
- <4a00dd60> Adapt popups to AnchorHandlers changes and add Alignment parameter to anchor-based positioning
- <1fce5d8a> MFXPopups: use Position for offsetting rather than Insets because it's more intuitive and easy to use



## 24.1.0 - 03-07-2025 - cc69b56a

### Features

- <712ebf4e> Add PivotUtils
- <c666ff4b> Add Corner enumeration
- <e5033d8b> MFXPopups: add onState(...) method
- <fb310d3a> CSSFragment: add appendSelector(...) method
- <68f3dbb2> AnchorHandlers: add ADJACENT_INWARDS position mode and make it default for popovers (technically for Node owners)
- <29fbbc4d> Implement convenience/facade API for easily show any kind of MFXPopup
- <52da6cb3> Add builder methods starting from given config to all MFXPopups
- <dd125b8e> Store and allow retrieval of applied config on MFXPopups
- <f9713573> Add Peer interface for MFXPopups' peers
- <49ecf06c> Add reposition mechanism to all MFXPopups and lock in place feature to MFXDialog
- <6c5ee750> Add always on top options for MFXDialogs
- <55f5068a> Add animation capabilities to MFXPopups
- <fb2397a8> MFXPopup: add convenience method to add a listener on the state property
- <995ff1a5> Add offset capabilities to MFXPopups
- <5193d452> Implemented MFXTooltip
- <ce663e20> Implemented MFXPopover
- <63e96298> Implemented MFXDialog
- <b6880422> Backport MFXBackdrop from effects module
- <9c9bb4c1> Implement position computation utility
- <9ee17cbe> Implementing the MFXPopup API
- <8b2931b7> CSSFragment: add method to append PseudoClasses
- <4f01f2cf> Implement measurement cache specifically for Labels
- <c16b298b> Complete review of Label
- <33e0fae0> Reviewed Size, Position and relative properties, as well as adding units support for StyleableSizeProperty and StyleablePositionProperty
- <b3e5df13> Replace TransformableLists with RefineList
- <3f99633e> Introduce the MFXSkinnable API, making the MVC pattern strategy more consistent
- <127d5b94> SkinBase: add convenience method to cast skinnable for skins that deal with subclasses of a control
- <82c34c4a> CSSFragment: add convenience method for transition duration
- <ecc63110> Backport MFXStyleable into this module with a few more utilities

### Bug Fixes

- <fd7c4d41> MFXPopover: set method was still overridden by CSS, use inline style to remove background and padding
- <10872c6c> AnchorHandler: fix positioning of dialogs in owner windows by using the owner's content bounds rather than the window bounds
- <6b7a92f9> MFXPopover: for some reason sometimes the state property is not set to HIDDEN, preventing it from showing. Fixed by adding a one-shot listener on the peer's showing property
- <65a6bb67> Label: fix text node detection and bind fontSmoothing
- <b880cda2> Styleable: fix UnsupportedOperationException

### Refactoring

- <82b5d0dd> MFXPopups: use root node for computing the position
- <bbe8edad> MFXDialog: bring to front if already showing
- <f44a735b> MFXPopover redirect auto-hide events to MFXPopup hide logic
- <72f0af1c> Label: do nothing when 'disableTruncation' is activated and the text Node is still null
- <3c705a2a> WithBehavior: make some methods default
- <fdf7e42f> CSSFragment: select class simple name if styleclasses list is empty
- <0e687a84> Label: change to onSetTextNode to a BiConsumer accepting the old text node too
- <aac2e612> Control and Labeled: add init() method to avoid boilerplate code
- <3e60831e> SkinBase: remove size computations shortcuts (it complicated in a way custom skin development) and make initBehavior() not abstract to avoid boilerplate code
- <dd44fc63> Selectable: make some methods defaults and add an optional isSelectable() method

### Documentation

- <bf8f94c3> Label: update documentation

