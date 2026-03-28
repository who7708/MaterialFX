# 波纹生成器

## MFXCircleRippleGenerators

- 样式类：mfx-ripple-generator

### 属性

| 属性              | 描述                                                                                                                                                                                                         | 类型     |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------:|
| region            | 将生成波纹的区域                                                                                                                                                                                             | Region   |
| clipSupplier      | 当前生成器的裁剪 Supplier。<br/>这负责创建生成器的裁剪节点，该节点在每次生成波纹时构建和设置，在动画开始之前，并定义波纹不能超出的边界。<br/>尽管供应商接受任何 Shape，但强烈建议使用 RippleClipTypeFactory 构建裁剪 | Supplier |
| rippleSupplier    | 生成器的波纹供应商。<br/>此 Supplier 负责在播放动画之前创建波纹形状                                                                                                                                          | Supplier |
| positionFunction  | 生成器的波纹位置函数。<br/>此 Function 负责在播放动画之前计算波纹的 X 和 Y 坐标。该函数以 MouseEvent 作为输入（因为在大多数控件中坐标是鼠标事件的 X 和 Y 坐标）并返回一个 PositionBean                               | Function |
| animateBackground | 指定是否也应动画化区域的背景。<br/>动画通常包括临时向生成器添加一个形状，将其填充设置为与波纹颜色相同，并使用时间轴操作其不透明度                                                                               | Boolean  |
| animateShadow     | 指定是否也应动画化区域的 DropShadow 效果。<br/>主要用于 MFXButtons                                                                                                                                            | Boolean  |
| checkBounds       | 指定在生成波纹之前是否应调用 isWithinBounds(MouseEvent) 方法。<br/>此属性的目的是禁用/绕过边界检查，在某些情况下可能需要禁用以使生成器正常工作。一个例子是 MFXCheckboxSkin                                      | Boolean  |
| depthLevelOffset  | 指定阴影应增加多少级别。<br/>例如，如果 DropShadow 效果是 DepthLevel.LEVEL1 且偏移量设置为 2，则阴影将变为 DepthLevel.LEVEL3（当然在动画结束时恢复）                                                           | Integer  |

### 可样式化属性

| 属性              | 描述                                                                                                                                  | CSS 属性                | 类型    | 默认值          |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------- | ----------------------- | -------:| ---------------:|
| animationSpeed    | 指定波纹动画的速度。<br/>这是通过设置动画的 rate 属性来实现的                                                                         | -mfx-animation-speed    | Double  | 1.0             |
| autoClip          | 指定生成器是否应尝试自动 buildClip()，这也意味着尝试获取背景/边框半径。<br/><b>实验性的，可能并非在所有情况下都有效</b>               | -mfx-auto-clip          | Boolean | false           |
| backgroundOpacity | 指定背景动画的强度                                                                                                                    | -mfx-background-opacity | Double  | 0.3             |
| rippleColor       | 指定波纹的颜色                                                                                                                        | -mfx-ripple-color       | Color   | Color.LIGHTGRAY |
| rippleOpacity     | 指定初始波纹的不透明度                                                                                                                | -mfx-ripple-opacity     | Double  | 1.0             |
| rippleRadius      | 指定波纹的初始半径                                                                                                                    | -mfx-ripple-radius      | Double  | 10.0            |
| paused            | 启用/禁用波纹生成器的属性                                                                                                             | -mfx-paused             | Boolean | false           |
