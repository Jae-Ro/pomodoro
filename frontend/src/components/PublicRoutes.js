import React, { Component } from 'react';
import { Route } from 'react-router-dom';
import Login from './Login';



class PublicRoutes extends Component {
    render() {
        return (
            <div className='PublicRoutes'>
                <Route exact path='/' component={Login} />
            </div>
        );
    }
}
export default PublicRoutes;
