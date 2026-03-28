# 上下文菜单

## MFXContextMenus

- 样式类：mfx-context-menu

- 默认样式表：MFXContextMenu.css

- 默认皮肤：MFXContextMenuSkin.java

### 属性

| 属性          | 描述                                                                                                                                                       | 类型           |
| ------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------:|
| items         | 包含上下文菜单项目的列表                                                                                                                                   | ObservableList |
| owner         | 上下文菜单的所有者节点                                                                                                                                     | Node           |
| disabled      | 启用/禁用上下文菜单                                                                                                                                        | Boolean        |
| showCondition | 指定用于确定 MouseEvent 是否应触发 showAction 属性的函数。<br/>默认情况下，检查是否按下了 SECONDARY 鼠标按钮                                               | Function       |
| showAction    | 指定当发生有效的 MouseEvent 时要执行的操作。<br> 默认情况下，在 MouseEvent 的屏幕坐标处显示上下文菜单                                                      | Consumer       |

### CSS 选择器

- .mfx-context-menu

- .mfx-context-menu .mfx-menu-item（访问菜单的项目）

## MFXContextMenuItem

- 样式类：mfx-menu-item

- 默认样式表：MFXContextMenuItem.css

- 默认皮肤：MFXContextMenuItemSkin.java

### 属性

| 属性            | 描述                                                                                                         | 类型         |
| --------------- | ------------------------------------------------------------------------------------------------------------ | ------------:|
| accelerator     | 指定加速器的文本。注意这只是文本，由用户设置所需的处理程序                                                   | String       |
| tooltipSupplier | 指定用于构建项目工具提示的 Supplier                                                                          | Supplier     |
| onAction        | 指定点击时要执行的操作                                                                                       | EventHandler |

### CSS 选择器

- .mfx-menu-item

- .mfx-menu-item .accelerator

- .mfx-menu-item .mfx-icon-wrapper（包含图标）
