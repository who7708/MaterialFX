# 步骤条

## 预览

<img src="https://imgur.com/nEgV9F1.gif" alt="步骤条">

## MFXSteppers

- 样式类：mfx-stepper

- 默认样式表：MFXStepper.css

- 默认皮肤：MFXStepperSkin.java

### 属性

| 属性                           | 描述                                                                                                                                                      | 类型           |
| ------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------:|
| stepperToggles                 | 步骤条的切换按钮列表，每个切换按钮是一个步骤                                                                                                              | ObservableList |
| animationDuration              | 指定进度条动画的持续时间，以毫秒为单位                                                                                                                    | Duration       |
| progress                       | 指定步骤条的进度，已完成的切换按钮数量除以切换按钮总数。值范围从 0.0 到 1.0                                                                               | Double         |
| currentIndex                   | 指定选中切换按钮在切换按钮列表中的位置。<br/>索引由 next() 和 previous() 方法更新                                                                         | Integer        |
| currentContent                 | 便利属性，保存选中切换按钮的内容节点。<br/>如果其中一个切换按钮的内容为 null，则清除内容面板的子项列表                                                    | Node           |
| lastToggle                     | 便利属性，指定是否选中了最后一个切换按钮                                                                                                                  | Boolean        |
| enableContentValidationOnError | 指定当按下下一步按钮且切换按钮状态为 ERROR 时，是否应验证所有实现 Validated 接口的控件                                                                    | Boolean        |

### 可样式化属性

| 属性                | 描述                                                                      | CSS 属性                | 类型      | 默认值               |
| ------------------- | ------------------------------------------------------------------------- | ----------------------- | ---------:| --------------------:|
| spacing             | 指定切换按钮之间的间距                                                    | -mfx-spacing            | Double    | 128.0                |
| extraSpacing        | 指定进度条的额外长度（在开始和结束处）                                    | -mfx-extra-spacing      | Double    | 64.0                 |
| alignment           | 指定切换按钮的对齐方式。<br/>步骤条通常居中                               | -mfx-alignment          | Pos[枚举] | Pos.CENTER           |
| baseColor           | 指定步骤条的主色                                                          | -mfx-base-color         | Paint     | Color.web("7F0FFF")  |
| altColor            | 指定步骤条的次要颜色                                                      | -mfx-alt-color          | Paint     | Color.web("BEBEBE")  |
| progressBarBorderRadius | 指定进度条的边框半径                                                  | -mfx-bar-borders-radius | Double    | 7.0                  |
| progressBarBackground | 指定进度条轨道颜色                                                      | -mfx-bar-background     | Paint     | Color.web("#F8F8FF") |
| progressColor       | 指定进度颜色                                                              | -mfx-progress-color     | Paint     | Color.web("#7F0FFF") |
| animated            | 指定进度条是否应该动画化                                                  | -mfx-bar-animated       | Boolean   | true                 |

### CSS 选择器

- .mfx-stepper

- .mfx-stepper .track（进度条的轨道）

- .mfx-stepper .bar（进度条的进度）

- .mfx-stepper .mfx-stepper-toggle

- .mfx-stepper .content-pane（包含切换按钮指定内容的面板）

- .mfx-stepper .buttons-box（包含上一步和下一步按钮的页脚）

- .mfx-stepper .buttons-box .mfx-button

## MFXStepperToggles

- 样式类：mfx-stepper-toggle

- 默认样式表：MFXStepperToggle.css

- 默认皮肤：MFXStepperToggleSkin.java

### 属性

| 属性          | 描述                                                                                                                                             | 类型                     |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------:|
| showErrorIcon | 指定当状态为 ERROR 时，是否应在切换按钮的右上角（皮肤定义的默认位置）显示一个小错误图标                                                            | Boolean                  |
| content       | 选中时要在步骤条中显示的内容                                                                                                                     | Node                     |
| text          | 指定要在切换按钮上方或下方显示的文本                                                                                                             | String                   |
| icon          | 指定要在切换按钮圆圈中显示的图标                                                                                                                 | Node                     |
| state         | 指定切换按钮的状态                                                                                                                               | StepperToggleState[枚举] |

### 可样式化属性

| 属性         | 描述                                                 | CSS 属性            | 类型               | 默认值              |
| ------------ | ---------------------------------------------------- | ------------------- | ------------------:| -------------------:|
| labelTextGap | 指定切换按钮圆圈和标签之间的间隙                     | -mfx-label-text-gap | Double             | 10.0                |
| textPosition | 指定标签的位置                                       | -mfx-text-position  | TextPosition[枚举] | TextPosition.BOTTOM |
| size         | 指定切换按钮圆圈的半径                               | -mfx-size           | Double             | 22.0                |
| strokeWidth  | 指定切换按钮圆圈的描边宽度                           | -mfx-stroke-width   | Double             | 2.0                 |

### CSS 选择器

- .mfx-stepper-toggle

- .mfx-stepper-toggle .mfx-text-field
