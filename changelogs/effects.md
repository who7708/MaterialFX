## 24.1.0 - 03-07-2025 - cc69b56a

### Features

- <e7fcfdd6> Introduce MFXBackdrop as the replacement for MFXScrimEffect
- <b88d25c2> Reviewed Size, Position and relative properties, as well as adding units support for StyleableSizeProperty and StyleablePositionProperty
- <b72d4c4a> Animations: add a few extra convenience methods to the AbstractBuilder
- <9c093f6a> Add Material Expressive motion curves

### Refactoring

- <1a8f68b7> AnimationFactory: generify factory by returning the array of KeyFrames rather than the Timeline, while keeping the convenience of build(...)
- <2629a82a> RippleGenerator: do not generate if Position is null
- <0165e785> Bind ripple color rather than set and update later. Useful for controls such as toggles which change their background according to the selection state
- <e5d8dc0e> ElevationLevel: LEVEL0 does not indicate null anymore, use NONE instead. Make animate() static
- <73c90be6> M3Motion: fix misspell

### Documentation

- <4995b7b7> Fix Javadoc error


