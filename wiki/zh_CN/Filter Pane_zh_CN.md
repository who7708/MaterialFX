# 过滤器面板

## MFXFilterPanes

- 样式类：mfx-filter-pane

- 默认样式表：MFXFilterPane.css

- 默认皮肤：MFXFilterPaneSkin.java

### 属性

| 属性          | 描述                                                                                                     | 类型           |
| ------------- | -------------------------------------------------------------------------------------------------------- | --------------:|
| headerText    | 指定标题的文本                                                                                           | String         |
| filters       | AbstractFilters 的列表。<br/>每个过滤器代表过滤器操作的对象字段                                          | ObservableList |
| activeFilters | 构建的过滤器列表                                                                                         | ObservableList |
| onFilter      | 点击过滤器图标时要执行的操作                                                                             | EventHandler   |
| onReset       | 点击重置图标时要执行的操作                                                                               | EventHandler   |

### CSS 选择器

- .mfx-filter-pane

- .mfx-filter-pane .header

- .mfx-filter-pane .header-label

- .mfx-filter-pane .header #filterIcon（包含波纹生成器，实际图标在 .mfx-font-icon）

- .mfx-filter-pane .header #resetIcon（包含波纹生成器，实际图标在 .mfx-font-icon）

- .mfx-filter-pane .filter-combo

- .mfx-filter-pane .predicates-combo

- .mfx-filter-pane .mfx-combo-box

- .mfx-filter-pane .mfx-text-field

- .mfx-filter-pane .mfx-button

- .mfx-filter-pane .mfx-scroll-pane

- .mfx-filter-pane .active-filter

- .mfx-filter-pane .active-filter .mfx-font-icon

- .mfx-filter-pane .active-filter .function-text

- .mfx-filter-pane .and-or-text
