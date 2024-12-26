
module.exports = (req, res, next) => {
    if (req.header("X-SPLINE-TIMEOUT")) {
        next()
    } else {
        res.sendStatus(598)
    }
}