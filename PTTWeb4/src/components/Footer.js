import React from 'react';
import Toolbar from '@material-ui/core/Toolbar';
// import Typography from '@material-ui/core/Typography';


import '../styles/footer.scss';

export default function ButtonAppBar() {

    return (
        <div className="footer">
            <Toolbar className="footer__toolbar">
                <p className="footer__toolbar--text">CS 6301 Web4 Team</p>
            </Toolbar>
        </div>
    );
}
