# 日期选择器

## 预览

<img src="https://imgur.com/J3v3i9w.gif" alt="选择器">

## MFXDatePickers

- 样式类：mfx-date-picker

- 默认样式表：MFXDatePicker.css

- 默认皮肤：MFXDatePickerSkin.java

- 默认单元格：MFXDateCell.java

### 属性

| 属性                       | 描述                                                                                                                                                              | 类型        |
| -------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- | -----------:|
| showing                    | 指定弹出窗口是否正在显示                                                                                                                                          | Boolean     |
| popupAlignment             | 指定弹出窗口的位置                                                                                                                                                | Alignment   |
| popupOffsetX               | 指定弹出窗口的 X 偏移量，要添加到计算出的 X 位置（来自 popupAlignment）的像素数                                                                                   | Double      |
| popupOffsetY               | 指定弹出窗口的 Y 偏移量，要添加到计算出的 Y 位置（来自 popupAlignment）的像素数                                                                                   | Double      |
| value                      | 指定当前选中的日期                                                                                                                                                | LocalDate   |
| converterSupplier          | 指定用于创建能够将 LocalDates 转换为字符串的 StringConverter 的 Supplier                                                                                          | Supplier    |
| monthConverterSupplier     | 指定用于创建能够将 Months 转换为字符串的 StringConverter 的 Supplier                                                                                              | Supplier    |
| dayOfWeekConverterSupplier | 指定用于创建能够将 DayOfWeeks 转换为字符串的 StringConverter 的 Supplier                                                                                          | Supplier    |
| cellFactory                | 指定用于在网格中创建日期单元格的函数                                                                                                                              | Function    |
| onCommit                   | 指定在组合框上按下 Enter 键时要执行的操作                                                                                                                         | Consumer    |
| onCancel                   | 指定在组合框上按下 Ctrl+Shift+Z 组合键时要执行的操作                                                                                                              | Consumer    |
| locale                     | 指定日期选择器使用的区域设置。<br/>区域设置主要负责更改语言和网格布局（例如不同的星期开始）                                                                       | Locale      |
| currentDate                | 指定当前日期                                                                                                                                                      | LocalDate   |
| yearsRange                 | 指定日期选择器的年份范围                                                                                                                                          | NumberRange |
| gridAlgorithm              | 指定用于生成月份网格的 BiFunction，该网格是一个整数值的二维数组                                                                                                   | BiFunction  |
| startingYearMonth          | 日期选择器将开始的 YearMonth。<br/> 注意这仅在第一次初始化时相关。之后设置此值不会产生任何效果                                                                    | YearMonth   |
| closePopupOnChange         | 值更改时弹出窗口应保持打开还是关闭                                                                                                                                | Boolean     |

### CSS 选择器

- .mfx-date-picker

- .mfx-date-picker .mfx-icon-wrapper（包含图标）

- .mfx-date-picker .mfx-icon-wrapper .mfx-ripple-generator

- .mfx-date-picker .mfx-icon-wrapper .mfx-font-icon（实际图标）

- .mfx-date-picker .date-picker-popup（访问弹出窗口）

- .mfx-date-picker .date-picker-popup .content（顶部容器，应该不需要，以下将省略）

- .mfx-date-picker .date-picker-popup .left-arrow（图标容器）

- .mfx-date-picker .date-picker-popup .left-arrow .mfx-ripple-generator

- .mfx-date-picker .date-picker-popup .left-arrow .mfx-font-icon（实际图标）

- .mfx-date-picker .date-picker-popup .right-arrow（图标容器）

- .mfx-date-picker .date-picker-popup .right-arrow .mfx-ripple-generator

- .mfx-date-picker .date-picker-popup .right-arrow .mfx-font-icon（实际图标）

- .mfx-date-picker .date-picker-popup .months-combo

- .mfx-date-picker .date-picker-popup .years-combo

- .mfx-date-picker .date-picker-popup .week-day

- .mfx-date-picker .date-picker-popup .mfx-date-cell
