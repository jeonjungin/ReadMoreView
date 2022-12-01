package com.willbegod.readmore.view


import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.willbegod.readmore.R

class ReadMoreView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        const val defaultBtnColor = Color.BLACK
        const val defaultMaxLine = Int.MAX_VALUE
        const val defaultColBtnText = "닫기"
        const val defaultExpBtnText = "더보기"
    }

    /**
     * 확장/축소 상태
     *
     * 상태 변경시 별다른 계산 없이 기 저장된 [displayColText] [displayExpText] 를 text에 바로 적용한다.
     *
     * */
    private var state = MoreState.COLLAPSED
        private set(value) {
            field = value
            text = when (value) {
                MoreState.COLLAPSED -> displayColText
                MoreState.EXPANDED -> displayExpText
            }
            stateChangedListener?.invoke(isExpanded)
        }

    val isExpanded: Boolean
        get () = state == MoreState.EXPANDED

    val isCollapsed: Boolean
        get () = state == MoreState.COLLAPSED

    /**
     * 원본 Text
     * ellipsize 처리 등이 되어있지 않음
     *
     * value 변경시 [updateDisplayText] 를 호출하여 [displayColText] [displayExpText] 를 업데이트하고, 현재 상태에 맞춰 text를 변경한다.
     *
     * */
    var originalText: String? = ""
        set (value) {
            if (field == value) {
                return
            }

            field = value
            updateDisplayText()
        }

    // ReadMoreView 가 확장 가능한 만큼의 텍스트를 가지고 있는지에 대한 여부
    private var isExpandable = false
        set (value) {
            if (field == value) {
                return
            }

            field = value
            expandableChangedListener?.invoke(value)
        }

    private var stateChangedListener: ((isExpended: Boolean) -> Unit)? = null

    private var expandableChangedListener: ((isExpandable: Boolean) -> Unit)? = null

    // TextView 축소시 표시할 최대 줄 갯수
    private var colMaxLine = defaultMaxLine
    private var collapseBtnText = defaultColBtnText
    private var expandBtnText = defaultExpBtnText
    @ColorInt
    private var btnColor = defaultBtnColor
    private var btnSizePx = textSize.toInt()

    // 확대/축소시 TextView 에 실제로 표시할 Text
    private var displayColText: SpannableStringBuilder? = null
    private var displayExpText: SpannableStringBuilder? = null
    private var oldTextWidth = 0

    init {
        initAttr(attrs)
        isClickable = true
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = ContextCompat.getColor(context, R.color.transparent)
    }

    private fun initAttr(attr: AttributeSet?) {
        attr?.let {
            with(context.obtainStyledAttributes(it, R.styleable.ReadMoreView)) {
                originalText = getString(R.styleable.ReadMoreView_originalText)
                colMaxLine = getInt(R.styleable.ReadMoreView_colMaxLines, defaultMaxLine)
                collapseBtnText = getString(R.styleable.ReadMoreView_collapseBtnText)?: defaultColBtnText
                expandBtnText = getString(R.styleable.ReadMoreView_expandBtnText)?: defaultExpBtnText
                btnColor = getColor(R.styleable.ReadMoreView_btnColor, defaultBtnColor)
                btnSizePx = getDimensionPixelSize(R.styleable.ReadMoreView_btnSize, textSize.toInt())

                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val givenWidth = MeasureSpec.getSize(widthMeasureSpec)
        val textWidth = givenWidth - compoundPaddingStart - compoundPaddingEnd
        if (textWidth == oldTextWidth) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        updateDisplayText(textWidth)

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * TextView 확장/축소시 표현할 Text 계산 및 입력
     *
     * 텍스트가 입력될 영역에 맞춰 [displayColText] [displayExpText] 를 계산하여 저장, Text 를 입력한다.
     *
     * @param textWidth 텍스트가 입력될 영역 너비
     *
     * */
    private fun updateDisplayText(textWidth: Int = measuredWidth - compoundPaddingStart - compoundPaddingEnd) {
        val expandLayout = getExpandStaticLayout(originalText?: "", textWidth)
        val isExpandable = expandLayout.lineCount > colMaxLine
        val expandContentText = "${expandLayout.text}"
        val expandSpannable = getContentSpannable(expandContentText, collapseBtnText, isExpandable).also {
            displayExpText = it
        }

        val collapseLayout = getCollapseStaticLayout(originalText?: "", textWidth, colMaxLine)
        val collapseContentText = "${collapseLayout.text}"

        // ellipsize 가 필요한 경우 추가 or 원본 (이모지가 삽입되는 경우 무조건 말줄임 표시 추가되어서 추가함)
        val ellipsizedOrNotText = if (isExpandable) {
            val totalLineWidth = (0 until colMaxLine).sumOf {
                collapseLayout.getLineWidth(it).toInt()
            }.toFloat()

            TextUtils.ellipsize(collapseContentText, paint, totalLineWidth, TextUtils.TruncateAt.END)
        } else {
            collapseContentText
        }

        val collapseSpannable = getContentSpannable(ellipsizedOrNotText.toString(), expandBtnText, isExpandable).also {
            displayColText = it
        }

        text = when (state) {
            MoreState.COLLAPSED -> collapseSpannable
            MoreState.EXPANDED -> expandSpannable
        }

        this.isExpandable = isExpandable
    }

    private fun getExpandStaticLayout(text: CharSequence, textWidth: Int): StaticLayout {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return StaticLayout.Builder
                .obtain(text, 0, text.length, paint, textWidth.coerceAtLeast(0))
                .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                .build()
        }

        return StaticLayout(text, paint, textWidth.coerceAtLeast(0), Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, true)
    }

    private fun getCollapseStaticLayout(text: CharSequence, textWidth: Int, maxLine: Int): StaticLayout {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return StaticLayout.Builder
                .obtain(text, 0, text.length, paint, textWidth.coerceAtLeast(0))
                .setEllipsize(TextUtils.TruncateAt.END)
                .setMaxLines(maxLine)
                .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                .build()
        }

        return StaticLayout(
            text,
            0,
            text.length,
            paint,
            textWidth.coerceAtLeast(0),
            Layout.Alignment.ALIGN_NORMAL,
            lineSpacingMultiplier,
            lineSpacingExtra,
            true,
            TextUtils.TruncateAt.END,
            textWidth.coerceAtLeast(0),
        )
    }

    /**
     * '더보기/닫기' 동작이 가능한 버튼이 포함된 Text 반환
     *
     * [btnColor] [btnSizePx] [btnText] 를 적용하여 버튼을 표시한다.
     *
     * @param content 원본 Text
     * @param btnText '더보기/닫기' 동작 버튼 Text
     * @param isExpandable '더보기/닫기' 동작이 가능한지에 대한 flag, false 일 경우 버튼을 표시하지 않는다.
     *
     * */
    private fun getContentSpannable(content: String, btnText: String, isExpandable: Boolean): SpannableStringBuilder {
        return SpannableStringBuilder().apply {
            append(content)

            if (btnText.isNotEmpty() && isExpandable) {
                append("\n")
                append(btnText)

                val btnStart = this.length - btnText.length
                val btnEnd = btnStart + btnText.length

                setSpan(UnderlineSpan(), btnStart, btnEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(AbsoluteSizeSpan(btnSizePx), btnStart, btnEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(object: ClickableSpan() {
                    override fun onClick(widget: View) {
                        toggle()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = btnColor
                    }
                }, btnStart, btnEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    /**
     * 확장/축소 토글
     *
     * 현재 [state] 에 따라 확장/축소를 실행한다.
     *
     * */
    fun toggle() {
        when (state) {
            MoreState.EXPANDED -> collapse()
            MoreState.COLLAPSED -> expand()
        }
    }

    fun collapse() {
        if (isCollapsed) {
            return
        }

        state = MoreState.COLLAPSED
    }

    fun expand() {
        if (isExpanded) {
            return
        }

        state = MoreState.EXPANDED
    }

    /**
     * 확장/축소 상태 변경 Callback
     * Listener 는 [state] 값 변경시 호출된다.
     *
     * @param listener [isExpanded] 값을 인자로 반환한다.
     *
     * */
    fun setStateChangedListener(listener: ((isExpended: Boolean) -> Unit)?) {
        this.stateChangedListener = listener
    }

    fun setExpandableChangedListener(listener: ((isExpandable: Boolean) -> Unit)?) {
        this.expandableChangedListener = listener
    }

    private enum class MoreState {
        COLLAPSED, EXPANDED
    }
}