export const normalize = (obj) => {
    if (!obj || typeof obj !== 'object') return obj
    const copy = { ...obj }
    Object.keys(copy).forEach((key) => {
        if (copy[key] === '') copy[key] = null
    })
    return copy
}