
function init() {
    setThemeColors();
    replaceArrow();
    document.body.style.display = "flow";
}

function setThemeColors() {
    const themeColors = JSON.parse(ThemeProvider.getThemeColors());
    for (const [key, value] of Object.entries(themeColors)) {
        document.body.style.setProperty(`--${key}`, value);
    }
}

function replaceArrow() {
    const arrowTemplate = `<div class="collapse-item-arrow">
    <i class="icon">
    <svg viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M5.64645 3.14645C5.45118 3.34171 5.45118 3.65829 5.64645 3.85355L9.79289 8L5.64645 12.1464C5.45118 12.3417 5.45118 12.6583 5.64645 12.8536C5.84171 13.0488 6.15829 13.0488 6.35355 12.8536L10.8536 8.35355C11.0488 8.15829 11.0488 7.84171 10.8536 7.64645L6.35355 3.14645C6.15829 2.95118 5.84171 2.95118 5.64645 3.14645Z" fill="currentColor">
    </path>
    </svg>
    </i>
    </div>`
    const collapseBtnList = document.querySelectorAll(".collapse_btn")
    collapseBtnList.forEach(collapseBtn => {
        collapseBtn.onclick = () => collapse(collapseBtn)
        collapseBtn.querySelectorAll('a[onclick="collapse(this);"]').forEach(anchor => {
            anchor.outerHTML = arrowTemplate; // 替换为箭头内容
        });
    })
    Date
}

function collapse(el) {
    // 切换折叠内容状态
    el.closest(".foldBox").classList.toggle("no")
}

function openImage(url) {
    window.NgaProvider.openImage(url)
}