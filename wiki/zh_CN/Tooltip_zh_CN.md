# 工具提示

## MFXTooltips

- 样式类：mfx-tooltip

- 默认样式表：MFXTooltip.css

- 默认皮肤：MFXTooltipSkin.java

### 属性

| 属性          | 描述                                                                                                                                                                                 | 类型         |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------:|
| icon          | 指定工具提示的图标                                                                                                                                                                   | Node         |
| text          | 指定工具提示的文本                                                                                                                                                                   | String       |
| owner         | 上下文菜单的所有者                                                                                                                                                                   | Node         |
| mousePosition | 上下文菜单跟踪所有者节点上的鼠标位置                                                                                                                                                 | PositionBean |
| showAction    | 此 Consumer 允许用户决定如何显示工具提示。<br/>Consumer 携带跟踪的鼠标位置，请参阅 (mousePosition)。<br/>默认情况下，调用 show(Node, double, double)                                   | Consumer     |
| showDelay     | 显示工具提示之前的时间量                                                                                                                                                             | Duration     |
| hideAfter     | 工具提示自动隐藏之前的时间量                                                                                                                                                         | Duration     |

### CSS 选择器

- .mfx-tooltip

- .mfx-tooltip .container（顶部容器，应该不需要）

- .mfx-tooltip .container .label
