import { onMounted, onBeforeUnmount } from 'vue'

export function useDanmu(texts = ['MessageCenter', 'Dasi', 'Mailbox']) {
    let timer = null

    const start = () => {
        const container = document.querySelector('.danmu-container')
        if (!container) return

        timer = setInterval(() => {
            const span = document.createElement('span')
            span.className = 'danmu-item'
            span.innerText = texts[Math.floor(Math.random() * texts.length)]

            // 随机高度
            span.style.top = Math.random() * 95 + 'vh'

            // 随机速度
            const duration = 5 + Math.random() * 7
            span.style.animationDuration = `${duration}s`

            // 随机透明度
            span.style.opacity = 0.10 + Math.random() * 0.15

            // 随机大小
            span.style.fontSize = `${30 + Math.random() * 25}px`

            container.appendChild(span)
            setTimeout(() => span.remove(), duration * 1000)
        }, 400)
    }

    onMounted(start)
    onBeforeUnmount(() => timer && clearInterval(timer))
}