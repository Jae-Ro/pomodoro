import React from 'react';
import ReactDOM from 'react-dom';
import UserReport from '../UserReport';
import renderer from 'react-test-renderer';
import {shallow} from 'enzyme';
import toJSON from 'enzyme-to-json';
import { Provider } from "react-redux";
import configureMockStore from "redux-mock-store";

const mockStore = configureMockStore();
const store = mockStore({});


test("UserReport matches its snapshot",()=>{
    const wrapper = shallow(<Provider store={store}> <UserReport /> </Provider>)

    expect(toJSON(wrapper)).toMatchSnapshot();
})